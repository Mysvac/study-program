package com.example.newsweb.controller;

import com.example.newsweb.dao.UserDAOImpl;
import com.example.newsweb.model.User;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;

@WebServlet(name = "LoginServlet", value = "/login-servlet")
public class LoginServlet extends HttpServlet {

    private UserDAOImpl userDAOImpl = new UserDAOImpl();

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            // 判断用户是否已注册
            boolean isRegistered = userDAOImpl.isUserRegistered(username);

            if (isRegistered) {
                // 如果已注册，验证用户名和密码
                boolean isValidUser = userDAOImpl.validateUser(username, password);

                if (isValidUser) {
                    // 登录成功，保存会话信息
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    session.setAttribute("isLoggedIn", true);    // 设置登录状态标志

                    // 如果勾选记住我，使用Cookie存储用户名和密码
                    if ("on".equals(request.getParameter("remember"))) {
                        Cookie usernameCookie = new Cookie("username", URLEncoder.encode(username, "UTF-8"));
                        Cookie passwordCookie = new Cookie("password", URLEncoder.encode(password, "UTF-8"));

                        usernameCookie.setMaxAge(60 * 60 * 24 * 30); // 30天
                        passwordCookie.setMaxAge(60 * 60 * 24 * 30);
                        response.addCookie(usernameCookie);
                        response.addCookie(passwordCookie);
                    }

                    response.sendRedirect("newsservlet");
                } else {
                    response.sendRedirect("login.jsp?error=1"); // 密码错误
                }
            } else {
                // 用户未注册，自动注册并登录
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword(password);

                // 注册新用户
                boolean registrationSuccess = userDAOImpl.registerUser(newUser);

                if (registrationSuccess) {
                    // 注册成功，进行登录
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    session.setAttribute("isLoggedIn", true);    // 设置登录状态标志

                    // 如果勾选记住我，使用Cookie存储用户名和密码
                    if ("on".equals(request.getParameter("remember"))) {
                        Cookie usernameCookie = new Cookie("username", URLEncoder.encode(username, "UTF-8"));
                        Cookie passwordCookie = new Cookie("password", URLEncoder.encode(password, "UTF-8"));

                        usernameCookie.setMaxAge(60 * 60 * 24 * 30); // 30天
                        passwordCookie.setMaxAge(60 * 60 * 24 * 30);
                        response.addCookie(usernameCookie);
                        response.addCookie(passwordCookie);
                    }

                    response.sendRedirect("newsservlet");
                } else {
                    response.sendRedirect("login.jsp?error=3"); // 注册失败
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("login.jsp?error=2"); // 其他错误
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // 转发到登录页面
            request.getRequestDispatcher("login.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");  // 错误处理页面
        }
    }
}
