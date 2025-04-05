<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="comment.css">
    <link rel="stylesheet" type="text/css" href="style.css">
    <script src="ad.js" defer></script>
    <!-- 核心库 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/core.min.js"></script>
    <!-- 编码支持 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/enc-utf8.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/enc-hex.min.js"></script>
    <!-- SHA-256 哈希算法 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/crypto-js/4.0.0/sha256.min.js"></script>

    <title>${news.title}</title>

</head>
<body>
<div class="news-detail">
    <span><a href="newsservlet">返回新闻列表</a></span>
    <h2>${news.title}</h2>
    <div class="detail-date">${news.date} &nbsp &nbsp浏览量：${news.view_count}
    </div>



    <div class="news-content">
        <c:forEach var="contentItem" items="${news.content}">
            <p class="indented">${contentItem}</p>
        </c:forEach>
    </div>

    <c:if test="${not empty news.imageLink}">
        <img src="${news.imageLink}" alt="新闻图片" />
    </c:if>

    <div class="news-author">
        <c:forEach var="author" items="${news.author}">
            <span>${author}</span>
        </c:forEach>
    </div>

    <span id="goods_type">${news.type}</span>

    <div class="comments">
        <!-- 评论显示区域 -->
        <c:forEach var="comment" items="${comments}">
            <div class="comment">
                <div class="comment-user">${comment.username}</div>
                <div class="comment-text">${comment.commentText}</div>
                <div class="comment-date"><fmt:formatDate value="${comment.date}" pattern="yyyy-MM-dd HH:mm:ss"/></div>
            </div>
        </c:forEach>

        <!-- 评论表单 -->
        <form action="news-detail" method="post">
            <!-- 隐藏字段传递新闻 ID -->
            <input type="hidden" name="id" value="${news.id}" />
            <textarea name="commentText" placeholder="请输入评论"></textarea>
            <button type="submit" id="comment-sub">提交评论</button>
        </form>
        <!-- 显示错误信息 -->
        <c:if test="${not empty param.error}">
            <div class="error-message">
                    ${param.error}
            </div>
        </c:if>
    </div>

</div>


</body>
</html>
