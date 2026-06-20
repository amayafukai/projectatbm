package com.ecommerce.servlet;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Product;
import com.ecommerce.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;

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
        String promotion = request.getParameter("promotion"); // có thể null

        // Lấy giỏ hàng từ session
        List<Product> cart = (List<Product>) session.getAttribute("cart");
        if (cart == null || cart.isEmpty()) {
            response.getWriter().println("Giỏ hàng trống!");
            return;
        }

        // 1. Xây dựng dữ liệu bất biến dạng chuỗi
        StringBuilder dataBuilder = new StringBuilder();
        // Thêm thông tin người mua
        dataBuilder.append("customer:").append(customerName).append("|");
        dataBuilder.append("address:").append(address).append("|");
        dataBuilder.append("promotion:").append(promotion != null ? promotion : "none").append("|");
        // Thêm danh sách sản phẩm
        for (Product p : cart) {
            dataBuilder.append("product:")
                       .append(p.getId()).append(",")
                       .append(p.getName()).append(",")
                       .append(p.getPrice()).append("|");
        }
        String rawData = dataBuilder.toString();
        System.out.println("Raw data to hash: " + rawData);

        // 2. Băm SHA-256
        String orderHash = null;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(rawData.getBytes("UTF-8"));
            // Chuyển thành chuỗi hex
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

        // 3. Lưu đơn hàng tạm (PENDING) vào database
        OrderDAO orderDAO = new OrderDAO();
        int orderId = orderDAO.createPendingOrder(userId, cart, promotion, customerName, address, orderHash);

        if (orderId == -1) {
            response.getWriter().println("Failed to save order");
            return;
        }

        // 4. Lưu orderHash vào session để dùng ở bước ký sau (hoặc có thể lấy lại từ DB)
        session.setAttribute("currentOrderHash", orderHash);
        session.setAttribute("currentOrderId", orderId);

        // 5. Chuyển hướng đến trang hiển thị hash và hướng dẫn ký
        response.sendRedirect("order_preview.jsp");
    }
}