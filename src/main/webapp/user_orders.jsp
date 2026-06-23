<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.ecommerce.model.Order, com.ecommerce.model.User" %>
<%
    User user = (User) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<Order> orders = (List<Order>) request.getAttribute("orders");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Đơn hàng của tôi</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .status-icon { font-size: 1.5rem; }
    </style>
</head>
<body>
<div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>📦 Đơn hàng của tôi</h2>
        <div>
            <span class="me-2">👋 Xin chào, <%= user.getName() %></span>
            <a href="index.jsp" class="btn btn-outline-primary btn-sm">← Trang chủ</a>
        </div>
    </div>

    <% if (orders == null || orders.isEmpty()) { %>
        <div class="alert alert-info">Bạn chưa có đơn hàng nào.</div>
    <% } else { %>
        <table class="table table-bordered table-striped">
            <thead class="table-dark">
                <tr>
                    <th>#</th>
                    <th>Sản phẩm</th>
                    <th>Giá</th>
                    <th>Số lượng</th>
                    <th>Ngày đặt</th>
                    <th>Trạng thái</th>
                </tr>
            </thead>
            <tbody>
                <% for (Order o : orders) { 
                    boolean verified = "VERIFIED".equals(o.getVerifyStatus());
                %>
                <tr>
                    <td><%= o.getId() %></td>
                    <td><%= o.getProductName() %></td>
                    <td>₹<%= o.getPrice() %></td>
                    <td><%= o.getQuantity() %></td>
                    <td><%= o.getOrderDate() %></td>
                    <td>
                        <% if (verified) { %>
                            <span class="status-icon text-success">✅</span> Đã xác thực
                        <% } else { %>
                            <span class="status-icon text-danger">❌</span> Chờ xác thực
                        <% } %>
                    </td>
                </tr>
                <% } %>
            </tbody>
        </table>
    <% } %>
</div>
</body>
</html>