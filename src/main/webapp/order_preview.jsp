<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="com.ecommerce.model.User"%>
<%
User user = (User) session.getAttribute("user");
if (user == null) {
	response.sendRedirect("login.jsp");
	return;
}

String orderHash = (String) session.getAttribute("currentOrderHash");
Integer orderId = (Integer) session.getAttribute("currentOrderId");

if (orderHash == null || orderId == null) {
	response.sendRedirect("index.jsp");
	return;
}
%>
<!DOCTYPE html>
<html>
<head>
<title>Xác nhận đơn hàng</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<style>
.hash-box {
	background: #f0f0f0;
	padding: 15px;
	border-radius: 8px;
	word-break: break-all;
	font-family: monospace;
}
</style>
</head>
<body>
	<div class="container mt-5">
		<div class="card p-4 shadow-sm">
			<h4>📄 Bước 1: Sao chép mã băm (Hash) của đơn hàng</h4>
			<p>Đây là mã đại diện cho thông tin đơn hàng của bạn. Bạn sẽ dùng
				mã này để ký số bằng công cụ riêng.</p>

			<div class="hash-box">
				<strong>Mã băm (SHA-256):</strong><br>
				<%=orderHash%>
			</div>

			<hr>

			<h5>🔐 Bước 2: Sử dụng tool ký (ứng dụng desktop)</h5>
			<ol>
				<li>Mở tool ký số (đã tải từ web).</li>
				<li>Dán mã băm vào ô dữ liệu.</li>
				<li>Chọn file <code>private.key</code> của bạn.
				</li>
				<li>Nhấn "Ký" và sao chép chữ ký (signature) được sinh ra.</li>
			</ol>

			<form action="verify-signature" method="post">
				<div class="mb-3">
					<label>Dán chữ ký (Base64) vào đây:</label>
					<textarea name="signature" class="form-control" rows="3" required></textarea>
				</div>
				<button type="submit" class="btn btn-success">✅ Xác nhận và
					hoàn tất đơn</button>
				<a href="index.jsp" class="btn btn-secondary">Hủy bỏ</a> <input
					type="hidden" name="orderId" value="<%=orderId%>" />
			</form>

			<input type="hidden" name="orderId" value="<%=orderId%>" />
		</div>
	</div>
</body>
</html>