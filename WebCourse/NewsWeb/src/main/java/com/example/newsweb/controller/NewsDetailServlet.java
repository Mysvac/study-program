package com.example.newsweb.controller;

import com.example.newsweb.dao.CommentDAOImpl;
import com.example.newsweb.dao.NewsDAOImpl;
import com.example.newsweb.model.Comment;
import com.example.newsweb.model.News;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "NewsDetailServlet", value = "/news-detail")
public class NewsDetailServlet extends HttpServlet {

    private NewsDAOImpl newsDAOImpl = new NewsDAOImpl();
    private CommentDAOImpl commentDAOImpl = new CommentDAOImpl();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 获取URL中的新闻ID参数
        String newsIdStr = request.getParameter("id");
        if (newsIdStr != null) {
            try {
                int newsId = Integer.parseInt(newsIdStr);

                // 调用 DAO 层的 getNewsById 方法获取新闻详情
                News news = newsDAOImpl.getNewsById(newsId);

                // 如果获取到新闻信息，传递给 JSP 页面显示
                if (news != null) {
                    // 更新浏览量
                    newsDAOImpl.incrementViewCount(newsId);

                    // 获取与新闻相关的评论
                    List<Comment> comments = commentDAOImpl.getCommentsByNewsId(newsId);
                    request.setAttribute("news", news);
                    request.setAttribute("comments", comments);
                    request.getRequestDispatcher("newsDetail.jsp").forward(request, response);
                } else {
                    // 如果没有找到该新闻，跳转到错误页面
                    response.sendRedirect("error.jsp");
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
                response.sendRedirect("error.jsp");
            } catch (SQLException e) {
                // 数据库连接错误
                e.printStackTrace();
                response.sendRedirect("error.jsp");
            }
        } else {
            // 如果没有传递 id 参数，跳转到错误页面
            response.sendRedirect("error.jsp");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        // 从 session 中获取登录状态
        Boolean isLoggedIn = (Boolean) request.getSession().getAttribute("isLoggedIn");

        // 如果用户没有登录
        if (isLoggedIn == null || !isLoggedIn) {
            // 重定向到登录页面
            response.sendRedirect("login.jsp?error");
            return;
        }

        // 获取 URL 中的新闻 ID 参数
        String newsIdStr = request.getParameter("id");
        String commentText = request.getParameter("commentText");

        // 检查评论内容和新闻 ID 是否有效
        if (newsIdStr == null || commentText == null || commentText.trim().isEmpty()) {
            // 如果没有提供新闻 ID 或评论内容，重定向回原页面并传递错误信息
            response.sendRedirect("news-detail?id=" + newsIdStr);
            return;
        }

        int newsId = Integer.parseInt(newsIdStr);

        // 从 session 中获取当前用户的用户名
        String username = (String) request.getSession().getAttribute("username");

        // 创建评论对象
        Comment comment = new Comment();
        comment.setNewsId(newsId);
        comment.setUsername(username);
        comment.setCommentText(commentText);

        // 保存评论
        boolean isSuccess = commentDAOImpl.addComment(comment);

        // 如果评论成功，重定向到新闻详情页面并携带新闻 ID
        if (isSuccess) {
            response.sendRedirect("news-detail?id=" + newsId);
        } else {
            // 如果评论失败，重定向到错误页面
            response.sendRedirect("error.jsp");
        }
    }
}
