package com.ecommerce.servlet;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Order;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@WebServlet("/verify-order-integrity")
public class VerifyOrderIntegrityServlet extends HttpServlet {
	 @Override
	    protected void doGet(HttpServletRequest request, HttpServletResponse response)
	            throws ServletException, IOException {

	        String orderIdStr = request.getParameter("orderId");
	        if (orderIdStr == null || orderIdStr.isEmpty()) {
	            response.getWriter().println("Thiếu ID đơn hàng.");
	            return;
	        }
	        int orderId = Integer.parseInt(orderIdStr);

	        OrderDAO dao = new OrderDAO();
	        Order firstOrder = dao.getOrderById(orderId);
	        if (firstOrder == null) {
	            response.getWriter().println("Không tìm thấy đơn hàng.");
	            return;
	        }

	        // Lấy tất cả sản phẩm trong cùng group
	        int groupId = firstOrder.getOrderGroupId();
	        List<Order> ordersInGroup = dao.getOrdersByGroupId(groupId);
	        if (ordersInGroup == null || ordersInGroup.isEmpty()) {
	            response.getWriter().println("Không có dữ liệu đơn hàng.");
	            return;
	        }

	        // Lấy hash gốc (lấy từ dòng đầu tiên)
	        String originalHash = firstOrder.getOrderHash();

	        // Tạo lại dữ liệu từ các cột hiện tại
	        StringBuilder dataBuilder = new StringBuilder();
	        // Lấy thông tin khách hàng từ dòng đầu (giống nhau cho cả group)
	        String customerName = firstOrder.getCustomerName();
	        String address = firstOrder.getAddress();
	        String promotion = firstOrder.getPromotion();
	        dataBuilder.append("customer:").append(customerName).append("|");
	        dataBuilder.append("address:").append(address).append("|");
	        dataBuilder.append("promotion:").append(promotion != null ? promotion : "none").append("|");
	        // Duyệt qua tất cả sản phẩm (sắp xếp theo id để đảm bảo thứ tự ổn định)
	        ordersInGroup.sort((o1, o2) -> Integer.compare(o1.getId(), o2.getId()));
	        for (Order o : ordersInGroup) {
	            dataBuilder.append("product:")
	                       .append(o.getProductId()).append(",")
	                       .append(o.getProductName()).append(",")
	                       .append(o.getPrice()).append(",")
	                       .append(o.getQuantity()).append("|");  
	        }
	        String currentData = dataBuilder.toString();

	        // Tính hash của dữ liệu hiện tại
	        String computedHash = sha256(currentData);

	        boolean isValid = originalHash.equals(computedHash);

	        // Gửi kết quả về JSP
	        request.setAttribute("order", firstOrder);
	        request.setAttribute("orders", ordersInGroup);
	        request.setAttribute("isValid", isValid);
	        request.setAttribute("computedHash", computedHash);
	        request.setAttribute("currentData", currentData); // để debug
	        request.getRequestDispatcher("order_integrity_result.jsp").forward(request, response);
	    }

	private String sha256(String input) {
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] hashBytes = md.digest(input.getBytes("UTF-8"));
			StringBuilder hexString = new StringBuilder();
			for (byte b : hashBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}