package com.example.newsweb.controller;

import com.example.newsweb.model.News;
import com.example.newsweb.service.NewsList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "NewsListServlet", value = "/newsservlet")
public class NewsListServlet extends HttpServlet {

    private NewsList newsListService = new NewsList();
    private static final int ITEMS_PER_PAGE = 10; // 每页显示的新闻条数

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        String searchQuery = request.getParameter("searchQuery");
        String type = request.getParameter("type"); // 根据类型筛选新闻
        String pageParam = request.getParameter("page");
        int currentPage = (pageParam != null && !pageParam.isEmpty()) ? Integer.parseInt(pageParam) : 1;

        try {
            List<News> allNews = null;

            // 如果有类型筛选
            if (type != null && !type.isEmpty()) {
                if ("上理要闻".equals(type)) {
                    allNews = newsListService.getNewsByType(""); // 空值处理
                } else {
                    allNews = newsListService.getNewsByType(type); // 根据实际类型查询
                }
            }
            // 如果有搜索关键词
            else if (searchQuery != null && !searchQuery.isEmpty()) {
                allNews = newsListService.searchNews(searchQuery);
            }
            // 如果没有筛选条件，默认显示所有新闻
            else {
                allNews = newsListService.getAllNews();
            }

            // 分页逻辑
            int totalItems = allNews.size(); // 总条目数
            int totalPages = (int) Math.ceil((double) totalItems / ITEMS_PER_PAGE); // 总页数

            // 计算当前页的新闻范围
            int startIndex = (currentPage - 1) * ITEMS_PER_PAGE;
            int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, totalItems);

            // 提取当前页的数据
            List<News> newsList = allNews.subList(startIndex, endIndex);

            // 设置请求属性
            request.setAttribute("newsList", newsList);
            request.setAttribute("currentPage", currentPage);
            request.setAttribute("totalPages", totalPages);

        } catch (SQLException e) {
            e.printStackTrace();
            response.sendRedirect("error.jsp");
            return;
        }

        // 转发到 JSP 页面显示结果
        request.getRequestDispatcher("newsList.jsp").forward(request, response);
    }
}
