package com.ecommerce.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.connection.DBConnection;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;

public class OrderDAO {

	// Place Order
	public boolean placeOrder(int userId, String name, String address, List<Product> cart) {
		boolean success = false;
		String sql = "INSERT INTO orders(user_id, product_id, product_name, price, quantity, customer_name, address) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			for (Product p : cart) {
				stmt.setInt(1, userId);
				stmt.setInt(2, p.getId());
				stmt.setString(3, p.getName());
				stmt.setDouble(4, p.getPrice());
				stmt.setInt(5, 1); // quantity = 1
				stmt.setString(6, name);
				stmt.setString(7, address);
				stmt.addBatch();
			}

			int[] result = stmt.executeBatch();
			success = result.length == cart.size();

		} catch (Exception e) {
			e.printStackTrace();
		}

		return success;
	}

	// Get Orders by User ID
	public List<Order> getOrdersByUserId(int userId) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				Order o = new Order();
				o.setId(rs.getInt("id"));
				o.setUserId(rs.getInt("user_id"));
				o.setProductId(rs.getInt("product_id"));
				o.setProductName(rs.getString("product_name"));
				o.setPrice(rs.getDouble("price"));
				o.setQuantity(rs.getInt("quantity"));
				o.setCustomerName(rs.getString("customer_name"));
				o.setAddress(rs.getString("address"));
				o.setOrderDate(rs.getTimestamp("order_date"));

				list.add(o);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// get admin id

	public List<Order> getAllOrders() {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT * FROM orders ORDER BY order_date DESC";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
				Order o = new Order();
				o.setId(rs.getInt("id"));
				o.setUserId(rs.getInt("user_id"));
				o.setProductId(rs.getInt("product_id"));
				o.setProductName(rs.getString("product_name"));
				o.setPrice(rs.getDouble("price"));
				o.setQuantity(rs.getInt("quantity"));
				o.setCustomerName(rs.getString("customer_name"));
				o.setAddress(rs.getString("address"));
				o.setOrderDate(rs.getTimestamp("order_date"));
				list.add(o);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// Lưu đơn hàng ở trạng thái PENDING (chưa ký) và trả về ID đơn hàng
	public int createPendingOrder(int userId, List<Product> cart, String promotion,
			String customerName, String address, String orderHash, int publicKeyId) {
		String sql = "INSERT INTO orders (user_id, product_name, quantity, price, promotion, " +
				"customer_name, address, order_hash, public_key_id, verify_status) " +
				"VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'PENDING')";
		int generatedId = -1;
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			for (Product p : cart) {
				ps.setInt(1, userId);
				ps.setString(2, p.getName());
				ps.setInt(3, 1); // quantity default 1
				ps.setDouble(4, p.getPrice());
				ps.setString(5, promotion);
				ps.setString(6, customerName);
				ps.setString(7, address);
				ps.setString(8, orderHash);
				ps.setInt(9, publicKeyId);
				ps.addBatch();
			}
			int[] results = ps.executeBatch();
			ResultSet rs = ps.getGeneratedKeys();
			if (rs.next()) {
				generatedId = rs.getInt(1);
			}
			return generatedId;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public Order getOrderById(int orderId) {
		String sql = "SELECT * FROM orders WHERE id = ?";
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, orderId);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Order o = new Order();
				o.setId(rs.getInt("id"));
				o.setUserId(rs.getInt("user_id"));
				o.setProductName(rs.getString("product_name"));
				o.setQuantity(rs.getInt("quantity"));
				o.setPrice(rs.getDouble("price"));
				o.setPromotion(rs.getString("promotion"));
				o.setOrderDate(rs.getTimestamp("order_date"));
				o.setOrderHash(rs.getString("order_hash"));
				o.setSignature(rs.getString("signature"));
				o.setPublicKeyId(rs.getInt("public_key_id")); // Lấy public_key_id
				o.setVerifyStatus(rs.getString("verify_status"));
				o.setCustomerName(rs.getString("customer_name"));
				o.setAddress(rs.getString("address"));
				return o;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Cập nhật chữ ký và trạng thái xác minh
	public boolean updateSignatureAndStatus(int orderId, String signature, String status) {
		String sql = "UPDATE orders SET signature = ?, verify_status = ? WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, signature);
			ps.setString(2, status);
			ps.setInt(3, orderId);
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

}
