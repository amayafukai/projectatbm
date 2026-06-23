package com.ecommerce.servlet;

import com.ecommerce.dao.UserKeyDAO;
import com.ecommerce.model.User;
import com.ecommerce.model.UserKey;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

@WebServlet("/revoke-key")
public class RevokeKeyServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        int userId = user.getId();

        UserKeyDAO keyDAO = new UserKeyDAO();
        UserKey activeKey = keyDAO.getActiveKey(userId);

        if (activeKey == null) {
            request.setAttribute("error", "Bạn chưa có khóa nào để thu hồi.");
            request.getRequestDispatcher("key_management.jsp").forward(request, response);
            return;
        }

        // 1. Thu hồi key cũ
        boolean revoked = keyDAO.revokeKey(activeKey.getId());
        if (!revoked) {
            request.setAttribute("error", "Lỗi khi thu hồi khóa cũ.");
            request.getRequestDispatcher("key_management.jsp").forward(request, response);
            return;
        }

        // 2. Tạo cặp khóa mới
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(2048);
            KeyPair keyPair = keyGen.generateKeyPair();
            PublicKey publicKey = keyPair.getPublic();
            PrivateKey privateKey = keyPair.getPrivate();

            String publicKeyBase64 = Base64.getEncoder().encodeToString(publicKey.getEncoded());
            boolean saved = keyDAO.savePublicKey(userId, publicKeyBase64);
            if (!saved) {
                response.getWriter().println("Lỗi khi lưu public key mới.");
                return;
            }

            // 3. Tải private key mới về máy người dùng
            String privateKeyBase64 = Base64.getEncoder().encodeToString(privateKey.getEncoded());
            String privateKeyPEM = "-----BEGIN PRIVATE KEY-----\n" +
                    privateKeyBase64 +
                    "\n-----END PRIVATE KEY-----";

            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment; filename=\"private_new.key\"");
            response.getWriter().write(privateKeyPEM);
            response.getWriter().flush();

            // 4. Thông báo thành công (sau khi tải file, không thể redirect, nên lưu vào session)
            session.setAttribute("keyRevoked", "✅ Đã thu hồi khóa cũ và tạo khóa mới. Vui lòng lưu private key mới!");

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Lỗi tạo khóa mới.");
        }
    }
}