<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page
	import="com.ecommerce.dao.UserKeyDAO, com.ecommerce.model.UserKey, com.ecommerce.model.User"%>
<%
User user = (User) session.getAttribute("user");
if (user == null) {
	response.sendRedirect("login.jsp");
	return;
}

UserKeyDAO keyDAO = new UserKeyDAO();
UserKey activeKey = keyDAO.getActiveKey(user.getId());

// Lấy thông báo từ session (nếu có)
String message = (String) session.getAttribute("keyGenerated");
if (message != null) {
	session.removeAttribute("keyGenerated");
}
%>
<!DOCTYPE html>
<html>
<head>
<title>Quản lý khóa</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<style>
.key-box {
	background: #f8f9fa;
	padding: 15px;
	border-radius: 5px;
	word-wrap: break-word;
	white-space: pre-wrap;
	font-family: monospace;
}
</style>
</head>
<body>
	<div class="container mt-5">
		<div class="d-flex justify-content-between align-items-center mb-4">
			<h2>🔐 Quản lý khóa của bạn</h2>
			<div>
				<span class="me-2">👋 Xin chào, <%=user.getName()%></span> <a
					href="index.jsp" class="btn btn-outline-primary btn-sm">← Trang
					chủ</a>
			</div>
		</div>

		<%
		if (message != null) {
		%>
		<div class="alert alert-success"><%=message%></div>
		<%
		}
		%>

		<div class="card p-4 shadow-sm">
			<h5>📌 Public Key hiện tại</h5>
			<%
			if (activeKey != null) {
			%>
			<div class="mb-3">
				<p>
					<strong>Public Key (Base64):</strong>
				</p>
				<div class="key-box"><%=activeKey.getPublicKey()%></div>
				<p class="mt-2">
					<strong>Trạng thái:</strong> <span class="badge bg-success"><%=activeKey.getStatus()%></span>
				</p>
				<p>
					<strong>Ngày tạo:</strong>
					<%=activeKey.getCreatedAt()%></p>
			</div>
			<%
			} else {
			%>
			<p class="text-muted">⚠️ Bạn chưa có public key nào. Hãy tạo mới.</p>
			<%
			}
			%>
			<hr>
			<div class="alert alert-warning">
				<strong>⚠️ Lưu ý:</strong> Nếu bạn nghi ngờ private key bị lộ, hãy
				báo mất khóa ngay lập tức. Hành động này sẽ thu hồi khóa hiện tại và
				tạo khóa mới cho bạn.
			</div>
			<a href="revoke-key" class="btn btn-danger"
				onclick="return confirm('Bạn có chắc muốn thu hồi khóa hiện tại?')">
				🚨 Báo mất khóa </a>


			<hr>
			<a href="generate-key" class="btn btn-primary">🔄 Tạo khóa mới</a>
			<p class="text-muted mt-2">
				<small>💡 Khi bấm nút, hệ thống sẽ sinh cặp khóa RSA mới.
					Public key sẽ được lưu trên hệ thống, Private key sẽ được tải về
					máy bạn dưới dạng file <code>private.key</code>. <br>⚠️ Vui
					lòng giữ file private key an toàn, không chia sẻ cho người khác.
				</small>
			</p>
		</div>
		<hr>
		<div class="mt-4">
			<h5>📥 Tải công cụ ký số</h5>
			<p>
				Tải về và chạy file
				<code>SignTool.jar</code>
				để ký hash đơn hàng bằng private key của bạn.
			</p>
			<a href="downloads/SignTool.jar" class="btn btn-success" download>
				⬇️ Tải SignTool.jar </a>
			<p class="text-muted mt-2">
				<small>Yêu cầu: Java Runtime Environment (JRE) đã cài đặt
					trên máy.</small>
			</p>
		</div>

	</div>
</body>
</html>