<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="zh">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" type="text/css" href="login.css">
    <title>登录</title>
</head>
<% request.setCharacterEncoding("GBK");%>
<body>

<div class="logincon">
<form action="login-servlet" method="post" onsubmit="return encryptFormData()">
    <h1>登录</h1>
    <label for="username">用户名:</label>
    <input type="text" id="username" name="username" required><br><br>
    <label for="password">密码:</label>
    <input type="password" id="password" name="password" required><br><br>
    <label for="remember">记住我</label>
    <input type="checkbox" id="remember" name="remember"><br><br>
    <button type="submit">登录</button>
</form>

<!-- 错误消息 -->
<c:if test="${not empty param.error}">
    <div class="error-message">
        <c:choose>
            <c:when test="${param.error == '1'}">
                <p>用户名或密码错误，请重新尝试。</p>
            </c:when>
            <c:when test="${param.error == '2'}">
                <p>发生未知错误，请稍后再试。</p>
            </c:when>
            <c:when test="${param.error == '3'}">
                <p>注册失败，请稍后再试。</p>
            </c:when>
            <c:otherwise>
                <p>出现未知的错误。</p>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>
</div>
</body>
</html>
