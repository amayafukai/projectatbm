<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, com.ecommerce.model.Order" %>
<%
    Order order = (Order) request.getAttribute("order");
    List<Order> orders = (List<Order>) request.getAttribute("orders");
    Boolean isValid = (Boolean) request.getAttribute("isValid");
    String computedHash = (String) request.getAttribute("computedHash");
    String currentData = (String) request.getAttribute("currentData");

    if (order == null || isValid == null) {
        response.sendRedirect("admin_dashboard.jsp");
        return;
    }
%>
<!DOCTYPE html>
<html>
<head>
    <title>Kiểm tra tính toàn vẹn đơn hàng</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        .hash-box { background: #f0f0f0; padding: 10px; border-radius: 5px; word-break: break-all; font-family: monospace; margin: 10px 0; }
        .data-box { background: #e9ecef; padding: 10px; border-radius: 5px; word-break: break-all; font-size: 12px; }
    </style>
</head>
<body>
<div class="container mt-5">
    <div class="card p-4 shadow-sm">
        <h3>🔍 Kiểm tra tính toàn vẹn đơn hàng #<%= order.getId() %></h3>
        <p><strong>Mã nhóm đơn:</strong> <%= order.getOrderGroupId() %></p>
        <hr>

        <h5>📦 Danh sách sản phẩm</h5>
        <table class="table table-bordered table-striped">
            <thead>
                <tr><th>#</th><th>Tên sản phẩm</th><th>Giá</th><th>Khuyến mãi</th></tr>
            </thead>
            <tbody>
                <% for (Order o : orders) { %>
                <tr>
                    <td><%= o.getId() %></td>
                    <td><%= o.getProductName() %></td>
                    <td>₹<%= o.getPrice() %></td>
                    <td><%= o.getPromotion() != null ? o.getPromotion() : "" %></td>
                </tr>
                <% } %>
            </tbody>
        </table>

        <h5>🧾 Thông tin khách hàng</h5>
        <p><strong>Tên:</strong> <%= order.getCustomerName() %></p>
        <p><strong>Địa chỉ:</strong> <%= order.getAddress() %></p>

        <hr>
        <h5>📌 Dữ liệu gốc (được dùng để tạo hash ban đầu):</h5>
        <div class="data-box"><%= currentData %></div>

        <h5>🔑 Hash gốc (lưu trong DB):</h5>
        <div class="hash-box"><%= order.getOrderHash() %></div>

        <h5>🔄 Hash tính lại từ dữ liệu hiện tại:</h5>
        <div class="hash-box"><%= computedHash %></div>

        <hr>
        <div class="mt-3">
            <h4>
                Kết quả:
                <% if (isValid) { %>
                    <span class="badge bg-success">✅ HỢP LỆ - Dữ liệu chưa bị thay đổi</span>
                <% } else { %>
                    <span class="badge bg-danger">❌ KHÔNG HỢP LỆ - Dữ liệu đã bị thay đổi!</span>
                <% } %>
            </h4>
        </div>

        <div class="mt-4">
            <a href="admin_dashboard.jsp" class="btn btn-secondary">Quay lại</a>
        </div>
    </div>
</div>
</body>
</html>