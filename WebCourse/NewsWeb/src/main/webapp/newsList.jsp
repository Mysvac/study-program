<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="style.css">
    <script src="ad.js" defer></script>
    <!-- 核心库 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/core.min.js"></script>
    <!-- 编码支持 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/enc-utf8.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/enc-hex.min.js"></script>
    <!-- SHA-256 哈希算法 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/sha256.min.js"></script>

    <title>上理工新闻</title>
</head>
<body>
<h1><a href="newsservlet">新闻列表</a></h1>

<!-- 导航栏 -->
<div class="navbar">
        <ul>
            <li><a href="newsservlet?action=type&type=电子产品">电子产品</a></li>
            <li><a href="newsservlet?action=type&type=家居用品">家居用品</a></li>
            <li><a href="newsservlet?action=type&type=服装服饰">服装服饰</a></li>
            <li><a href="newsservlet?action=type&type=美妆护肤">美妆护肤</a></li>
            <li><a href="newsservlet?action=type&type=食品饮料">食品饮料</a></li>
            <li><a href="newsservlet?action=type&type=汽车交通">汽车交通</a></li>
            <li><a href="newsservlet?action=type&type=旅游出行">旅游出行</a></li>
            <li><a href="newsservlet?action=type&type=上理要闻">上理要闻</a></li>
        </ul>

</div>

<%--登录--%>
<c:choose>
    <c:when test="${sessionScope.isLoggedIn}">
        <a href="logout-servlet" class="logout-link">注销</a>
        <span class="login-link">已登录</span>
    </c:when>
    <c:otherwise>
        <a href="login.jsp" class="login-link">登录</a>
    </c:otherwise>
</c:choose>

<%--搜索框--%>
<form action="newsservlet" method="get">
    <input type="text" name="searchQuery" placeholder="请输入标题搜索" />
    <button type="submit">搜索</button>
</form>
<br>

<!-- 遍历并展示新闻列表 -->
<c:forEach var="news" items="${newsList}">
<%--    预览卡片--%>
    <div class="news-preview">
        <div class="news-date">${news.date}  &nbsp&nbsp浏览量：${news.view_count}</div><br><br>
        <a href="news-detail?id=${news.id}" class="news-title">${news.title}</a>
        <br><br><br>

        <div class="pre-container">
            <div class="news-summary">${news.summary}</div>
            <c:if test="${not empty news.imageLink}">
                <img src="${news.imageLink}" alt="News Image"/>
            </c:if>

        </div>
    </div>
</c:forEach>

<!-- 分页 -->
<div class="pagination">
    <c:forEach var="page" begin="1" end="${totalPages}">
        <a href="newsservlet?page=${page}" class="${currentPage == page ? 'active' : ''}">${page}</a>
    </c:forEach>
</div>

<style>
    .pagination {
        margin: 20px 0;
        text-align: center;
    }
    .pagination a {
        margin: 0 5px;
        text-decoration: none;
        color: #007bff;
        font-size: 1.2rem;
        padding: 5px 10px;
        border: 1px solid #ddd;
        border-radius: 4px;
        background-color: #ffffff;
    }
    .pagination a.active {
        background-color: #007bff;
        color: white;
        border-color: #007bff;
    }
    .pagination a:hover {
        background-color: #0056b3;
        color: white;
    }
</style>
</body>
</html>