package com.example.newsweb.controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "LogoutServlet", value = "/logout-servlet")
public class LogoutServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 清除会话中的登录信息
        HttpSession session = request.getSession();
        session.invalidate();

        // 重定向到新闻列表页面
        response.sendRedirect("newsservlet");
    }
}
