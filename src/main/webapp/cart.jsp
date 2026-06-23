<%@ page contentType="text/html;charset=UTF-8" import="java.util.*, com.ecommerce.model.CartItem, com.ecommerce.model.Product" %>
<%
    if (session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }
    List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
%>
<!DOCTYPE html>
<html>
<head>
    <title>Your Cart</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body>
<div class="container mt-5">
    <h2 class="mb-3">Your Cart</h2>
    <%
        double total = 0;
        if (cart != null && !cart.isEmpty()) {
    %>
    <table class="table table-bordered">
        <thead>
            <tr><th>Sản phẩm</th><th>Giá</th><th>Số lượng</th><th>Tổng</th></tr>
        </thead>
        <tbody>
            <% for (CartItem item : cart) {
                   Product p = item.getProduct();
                   double subtotal = p.getPrice() * item.getQuantity();
                   total += subtotal;
            %>
            <tr>
                <td><%= p.getName() %></td>
                <td>₹<%= p.getPrice() %></td>
                <td><%= item.getQuantity() %></td>
                <td>₹<%= subtotal %></td>
            </tr>
            <% } %>
            <tr>
                <td colspan="3"><strong>Tổng cộng</strong></td>
                <td><strong>₹<%= total %></strong></td>
            </tr>
        </tbody>
    </table>
    <a href="checkout.jsp" class="btn btn-success">Checkout</a>
    <% } else { %>
        <div class="alert alert-warning">Giỏ hàng trống.</div>
    <% } %>
</div>
</body>
</html>