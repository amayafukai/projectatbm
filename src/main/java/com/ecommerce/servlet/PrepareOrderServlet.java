package com.ecommerce.servlet;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.UserKeyDAO;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import com.ecommerce.model.UserKey;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@WebServlet("/prepare-order")
public class PrepareOrderServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        String customerName = request.getParameter("name");
        String address = request.getParameter("address");
        String promotion = request.getParameter("promotion");

        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            response.getWriter().println("Giỏ hàng trống!");
            return;
        }

        // Lấy public key đang hoạt động
        UserKeyDAO keyDAO = new UserKeyDAO();
        UserKey activeKey = keyDAO.getActiveKey(userId);
        if (activeKey == null) {
            response.getWriter().println("Bạn chưa có public key. Vui lòng tạo khóa trước.");
            return;
        }
        int publicKeyId = activeKey.getId();

        // Xây dựng dữ liệu bất biến (order_details)
        StringBuilder dataBuilder = new StringBuilder();
        dataBuilder.append("customer:").append(customerName).append("|");
        dataBuilder.append("address:").append(address).append("|");
        dataBuilder.append("promotion:").append(promotion != null ? promotion : "none").append("|");
        for (Product p : cart) {
            dataBuilder.append("product:")
                       .append(p.getId()).append(",")
                       .append(p.getName()).append(",")
                       .append(p.getPrice()).append("|");
        }
        String orderDetails = dataBuilder.toString();

        // Băm SHA-256
        String orderHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(orderDetails.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            orderHash = hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            response.getWriter().println("Lỗi băm dữ liệu.");
            return;
        }

        // Tạo order_group_id (dùng timestamp hoặc ngẫu nhiên)
        int orderGroupId = (int) (System.currentTimeMillis() / 1000);

        // Lưu đơn hàng
        OrderDAO orderDAO = new OrderDAO();
        int orderId = orderDAO.createPendingOrder(userId, cart, promotion, customerName, address,
                                                  orderHash, publicKeyId, orderDetails, orderGroupId);

        if (orderId == -1) {
            response.getWriter().println("Failed to save order");
            return;
        }

        session.setAttribute("currentOrderHash", orderHash);
        session.setAttribute("currentOrderId", orderId);

        response.sendRedirect("order_preview.jsp");
    }
}