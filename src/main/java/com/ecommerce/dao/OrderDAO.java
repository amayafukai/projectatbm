package com.ecommerce.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.ecommerce.connection.DBConnection;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Order;
import com.ecommerce.model.Product;

public class OrderDAO {

	// Lấy tất cả đơn hàng (cho admin)
	public List<Order> getAllOrders() {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT id, user_id, product_name, quantity, price, promotion, "
				+ "order_date, order_hash, signature, public_key_id, verify_status, "
				+ "customer_name, address, order_details, order_group_id, product_id "
				+ "FROM orders ORDER BY order_date DESC";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {

			while (rs.next()) {
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
				o.setPublicKeyId(rs.getInt("public_key_id"));
				o.setVerifyStatus(rs.getString("verify_status"));
				o.setCustomerName(rs.getString("customer_name"));
				o.setAddress(rs.getString("address"));
				o.setOrderDetails(rs.getString("order_details"));
				o.setOrderGroupId(rs.getInt("order_group_id"));
				o.setProductId(rs.getInt("product_id"));
				list.add(o);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// Lấy đơn hàng theo user
	public List<Order> getOrdersByUserId(int userId) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT id, user_id, product_name, quantity, price, promotion, "
				+ "order_date, order_hash, signature, public_key_id, verify_status, "
				+ "customer_name, address, order_details, order_group_id, product_id "
				+ "FROM orders WHERE user_id = ? ORDER BY order_date DESC";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

			stmt.setInt(1, userId);
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
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
				o.setPublicKeyId(rs.getInt("public_key_id"));
				o.setVerifyStatus(rs.getString("verify_status"));
				o.setCustomerName(rs.getString("customer_name"));
				o.setAddress(rs.getString("address"));
				o.setOrderDetails(rs.getString("order_details"));
				o.setOrderGroupId(rs.getInt("order_group_id"));
				o.setProductId(rs.getInt("product_id"));
				list.add(o);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return list;
	}

	// Lấy đơn hàng theo ID
	public Order getOrderById(int orderId) {
		String sql = "SELECT id, user_id, product_name, quantity, price, promotion, "
				+ "order_date, order_hash, signature, public_key_id, verify_status, "
				+ "customer_name, address, order_details, order_group_id, product_id " + "FROM orders WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
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
				o.setPublicKeyId(rs.getInt("public_key_id"));
				o.setVerifyStatus(rs.getString("verify_status"));
				o.setCustomerName(rs.getString("customer_name"));
				o.setAddress(rs.getString("address"));
				o.setOrderDetails(rs.getString("order_details"));
				o.setOrderGroupId(rs.getInt("order_group_id"));
				o.setProductId(rs.getInt("product_id"));
				return o;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// Tạo đơn hàng mới (trạng thái PENDING)
	public int createPendingOrder(int userId, List<CartItem> cart, String promotion, String customerName,
			String address, String orderHash, int publicKeyId, String orderDetails, int orderGroupId) {
		String sql = "INSERT INTO orders (user_id, product_name, quantity, price, promotion, "
				+ "customer_name, address, order_hash, public_key_id, verify_status, "
				+ "order_details, order_group_id, product_id) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		int generatedId = -1;
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			for (CartItem item : cart) {
				Product p = item.getProduct();
				ps.setInt(1, userId);
				ps.setString(2, p.getName());
				ps.setInt(3, item.getQuantity()); // ✅ quantity từ CartItem
				ps.setDouble(4, p.getPrice());
				ps.setString(5, promotion);
				ps.setString(6, customerName);
				ps.setString(7, address);
				ps.setString(8, orderHash);
				ps.setInt(9, publicKeyId);
				ps.setString(10, "PENDING");
				ps.setString(11, orderDetails);
				ps.setInt(12, orderGroupId);
				ps.setInt(13, p.getId());
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

	// Cập nhật chữ ký và trạng thái cho toàn bộ nhóm đơn hàng (theo order_group_id)
	public boolean updateSignatureAndStatusByGroup(int orderGroupId, String signature, String status) {
		String sql = "UPDATE orders SET signature = ?, verify_status = ? WHERE order_group_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setString(1, signature);
			ps.setString(2, status);
			ps.setInt(3, orderGroupId);
			int rows = ps.executeUpdate();
			return rows > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public List<Order> getOrdersByGroupId(int orderGroupId) {
		List<Order> list = new ArrayList<>();
		String sql = "SELECT id, user_id, product_name, quantity, price, promotion, "
				+ "order_date, order_hash, signature, public_key_id, verify_status, "
				+ "customer_name, address, order_details, order_group_id, product_id "
				+ "FROM orders WHERE order_group_id = ? ORDER BY id";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, orderGroupId);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
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
				o.setPublicKeyId(rs.getInt("public_key_id"));
				o.setVerifyStatus(rs.getString("verify_status"));
				o.setCustomerName(rs.getString("customer_name"));
				o.setAddress(rs.getString("address"));
				o.setOrderDetails(rs.getString("order_details"));
				o.setOrderGroupId(rs.getInt("order_group_id"));
				o.setProductId(rs.getInt("product_id"));
				list.add(o);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
}