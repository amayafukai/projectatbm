package com.ecommerce.servlet;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.dao.UserKeyDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import com.ecommerce.model.UserKey;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.sql.Timestamp;
import java.util.Base64;

@WebServlet("/verify-signature")
public class VerifySignatureServlet extends HttpServlet {
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

        String orderIdStr = request.getParameter("orderId");
        if (orderIdStr == null || orderIdStr.isEmpty()) {
            response.getWriter().println("Thiếu ID đơn hàng.");
            return;
        }
        int orderId = Integer.parseInt(orderIdStr);

        String signatureBase64 = request.getParameter("signature");
        if (signatureBase64 == null || signatureBase64.trim().isEmpty()) {
            response.getWriter().println("Vui lòng nhập chữ ký.");
            return;
        }

        OrderDAO orderDAO = new OrderDAO();
        Order order = orderDAO.getOrderById(orderId);
        if (order == null) {
            response.getWriter().println("Không tìm thấy đơn hàng.");
            return;
        }

        if (order.getUserId() != userId) {
            response.getWriter().println("Bạn không có quyền xác nhận đơn hàng này.");
            return;
        }

        String orderHash = order.getOrderHash();
        if (orderHash == null || orderHash.isEmpty()) {
            response.getWriter().println("Đơn hàng chưa có hash.");
            return;
        }

        // Lấy public key theo public_key_id đã lưu trong đơn hàng
        int publicKeyId = order.getPublicKeyId();
        UserKeyDAO keyDAO = new UserKeyDAO();
        UserKey userKey = keyDAO.getKeyById(publicKeyId); // Cần viết method này
        if (userKey == null) {
            response.getWriter().println("Không tìm thấy public key tương ứng với đơn hàng.");
            return;
        }

        // Kiểm tra thời gian thu hồi (revoke)
        Timestamp revokedAt = userKey.getRevokedAt();
        if (revokedAt != null) {
            Timestamp orderTime = order.getOrderDate();
            if (orderTime != null && orderTime.after(revokedAt)) {
                // Đơn hàng được tạo sau khi key bị thu hồi -> không hợp lệ
                request.setAttribute("error", "❌ Public key đã bị thu hồi trước thời điểm tạo đơn hàng. Đơn hàng không hợp lệ.");
                request.getRequestDispatcher("verify_result.jsp").forward(request, response);
                return;
            }
        }

        String publicKeyBase64 = userKey.getPublicKey();

        try {
            byte[] keyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(spec);

            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64.trim());
            byte[] hashBytes = hexStringToByteArray(orderHash);

            Signature sig = Signature.getInstance("SHA256withRSA");
            sig.initVerify(publicKey);
            sig.update(hashBytes);
            boolean verified = sig.verify(signatureBytes);

            if (verified) {
            	 // Lấy orderGroupId từ order đã lấy được
                int groupId = order.getOrderGroupId();
                boolean updated = orderDAO.updateSignatureAndStatusByGroup(groupId, signatureBase64.trim(), "VERIFIED");
                if (updated) {
                    session.removeAttribute("currentOrderHash");
                    session.removeAttribute("currentOrderId");
                    request.setAttribute("message", "✅ Đơn hàng đã được xác minh và hoàn tất!");
                    request.getRequestDispatcher("verify_result.jsp").forward(request, response);
                } else {
                    response.getWriter().println("Lỗi khi cập nhật đơn hàng.");
                }
            } else {
                request.setAttribute("error", "❌ Chữ ký không hợp lệ. Vui lòng kiểm tra lại private key hoặc hash.");
                request.getRequestDispatcher("verify_result.jsp").forward(request, response);
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("Lỗi xác minh: " + e.getMessage());
        }
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                                 + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}