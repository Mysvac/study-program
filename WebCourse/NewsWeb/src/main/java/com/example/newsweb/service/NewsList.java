package com.example.newsweb.service;

import com.example.newsweb.dao.NewsDAO;
import com.example.newsweb.dao.NewsDAOImpl;
import com.example.newsweb.model.News;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public class NewsList {

    private NewsDAO newsDAO;


    public NewsList() {
        this.newsDAO = new NewsDAOImpl();
    }

    // 获取所有新闻并按类型分类
    public List<News> getNewsByType(String type) throws SQLException {
        return newsDAO.getNewsByType(type); // 获取所有新闻并按类型分类
    }
    // 根据月份获取新闻
    public List<News> getNewsByMonth(String month) throws SQLException {
        return newsDAO.getNewsByMonth(month);
    }

    // 根据搜索关键词搜索新闻
    public List<News> searchNews(String searchQuery) throws SQLException {
        return newsDAO.searchNews(searchQuery);
    }

    // 获取所有新闻
    public List<News> getAllNews() throws SQLException {
        return newsDAO.getAllNews();
    }
}
