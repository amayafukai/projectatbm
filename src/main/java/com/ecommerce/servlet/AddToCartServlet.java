package com.ecommerce.servlet;

import com.ecommerce.dao.ProductDAO;
import com.ecommerce.model.CartItem;
import com.ecommerce.model.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/add-to-cart")
public class AddToCartServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String idParam = request.getParameter("id");
        String qtyParam = request.getParameter("quantity");

        if (idParam == null || idParam.isEmpty()) {
            response.sendRedirect("index.jsp");
            return;
        }

        int id = Integer.parseInt(idParam);
        int quantity = 1;
        if (qtyParam != null && !qtyParam.isEmpty()) {
            try {
                quantity = Integer.parseInt(qtyParam);
                if (quantity < 1) quantity = 1;
            } catch (NumberFormatException e) {}
        }

        ProductDAO dao = new ProductDAO();
        Product product = null;
        try {
            product = dao.getProductById(id);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("index.jsp");
            return;
        }

        if (product != null) {
            HttpSession session = request.getSession();
            List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
            if (cart == null) cart = new ArrayList<>();

            // Cập nhật số lượng nếu sản phẩm đã có trong giỏ
            boolean found = false;
            for (CartItem item : cart) {
                if (item.getProduct().getId() == id) {
                    item.setQuantity(item.getQuantity() + quantity);
                    found = true;
                    break;
                }
            }
            if (!found) {
                cart.add(new CartItem(product, quantity));
            }
            session.setAttribute("cart", cart);
        }
        response.sendRedirect("cart.jsp");
    }
}