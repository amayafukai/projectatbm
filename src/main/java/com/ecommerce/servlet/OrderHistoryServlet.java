package com.ecommerce.servlet;

import com.ecommerce.dao.OrderDAO;
import com.ecommerce.model.Order;
import com.ecommerce.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet("/order-history")
public class OrderHistoryServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        User user = (User) session.getAttribute("user");
        OrderDAO dao = new OrderDAO();
        List<Order> orders = dao.getOrdersByUserId(user.getId());

        // Nhóm theo order_group_id
        Map<Integer, List<Order>> groupMap = new LinkedHashMap<>();
        for (Order o : orders) {
            int groupId = o.getOrderGroupId();
            groupMap.computeIfAbsent(groupId, k -> new ArrayList<>()).add(o);
        }

        // Chuyển Map thành List để dễ duyệt
        List<Map.Entry<Integer, List<Order>>> groupedOrders = new ArrayList<>(groupMap.entrySet());
        // Sắp xếp theo groupId giảm dần (mới nhất trước)
        groupedOrders.sort((e1, e2) -> e2.getKey().compareTo(e1.getKey()));

        request.setAttribute("groupedOrders", groupedOrders);
        request.getRequestDispatcher("order_history.jsp").forward(request, response);
    }
}