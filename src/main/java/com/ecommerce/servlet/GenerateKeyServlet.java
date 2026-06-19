package com.ecommerce.servlet;

import com.ecommerce.dao.UserKeyDAO;
import com.ecommerce.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@WebServlet("/generate-key")
public class GenerateKeyServlet extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		HttpSession session = request.getSession(false);
		if (session == null || session.getAttribute("user") == null) {
			response.sendRedirect("login.jsp");
			return;
		}

		User user = (User) session.getAttribute("user");
		int userId = user.getId();

		try {
			// 1. Tạo cặp khóa RSA 2048 bit
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			keyGen.initialize(2048);
			KeyPair keyPair = keyGen.generateKeyPair();
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();

			// 2. Mã hóa Public Key thành chuỗi Base64
			String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());

			// 3. Thu hồi các key cũ (nếu có), sau đó lưu public key mới với status ACTIVE
			UserKeyDAO keyDAO = new UserKeyDAO();
			keyDAO.revokeAllKeys(userId); // đánh dấu key cũ là REVOKED
			boolean saved = keyDAO.savePublicKey(userId, publicKeyBase64);
			if (!saved) {
				response.getWriter().println("Lỗi: Không thể lưu public key.");
				return;
			}

			// 4. Chuẩn bị private key để tải về (dạng PEM)
			String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
			String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" + privateKeyBase64 + "\n-----END PRIVATE KEY-----";

			// 5. Thiết lập response để tải file private.key
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename=\"private.key\"");

			// Ghi nội dung private key vào response
			response.getWriter().write(privateKeyPEM);
			response.getWriter().flush();

			// (Không thể redirect sau khi đã ghi dữ liệu)
			// Ta dùng session để thông báo thành công khi trang JSP load lại
			session.setAttribute("keyGenerated", "✅ Đã tạo khóa mới và tải private key thành công!");

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			response.getWriter().println("Lỗi: Không thể tạo cặp khóa.");
		}
	}
}