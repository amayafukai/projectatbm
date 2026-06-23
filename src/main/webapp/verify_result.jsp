<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Kết quả xác minh</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <div class="card p-4 shadow-sm">
        <h4 class="text-center">🔐 Kết quả xác minh chữ ký</h4>

        <% if (request.getAttribute("message") != null) { %>
            <div class="alert alert-success"><%= request.getAttribute("message") %></div>
            <div class="text-center">
                <a href="user-orders" class="btn btn-primary">Xem lịch sử đơn hàng</a>
                <a href="index.jsp" class="btn btn-secondary">Tiếp tục mua sắm</a>
            </div>
        <% } else if (request.getAttribute("error") != null) { %>
            <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
            <div class="text-center">
                <a href="order_preview.jsp" class="btn btn-warning">Quay lại nhập lại chữ ký</a>
                <a href="index.jsp" class="btn btn-secondary">Hủy</a>
            </div>
        <% } %>
    </div>
</div>
</body>
</html>