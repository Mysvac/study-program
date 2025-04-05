package com.asaki0019.advertising.utils;

import com.asaki0019.advertising.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils.class);

    Utils() {
    }

    /**
     * 从HttpSession中获取用户信息
     * 此方法尝试从HttpSession对象中检索存储的用户信息它期望在session中找到一个标记为"user"的属性，
     * 并将其转换为User对象类型如果session中没有用户信息，或者转换过程中遇到错误，
     * 则记录一个错误日志，并返回null
     *
     * @param session HttpSession对象，用于存储用户会话信息
     * @return User对象，表示从session中获取的用户信息如果没有找到用户信息或转换失败，则返回null
     */
    public static User getUserFromSession(HttpSession session) {
        try {
            var user = (User) session.getAttribute("user");
            if (user == null) {
                logError("从HttpSession中获取用户信息失败: session id " + session.getId(), null, "Utils");
                return null;
            }
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isNotUserLoggedIn(String jwt) {
        var user = validateJWT(jwt);
        if (user == null) {
            log(LogLevel.INFO, "用户未登录", null, "Utils");
            return true;
        }
        return false;
    }

    /**
     * 验证JWT并返回Claims
     *
     * @param token JWT字符串
     * @return 解析后的Claims对象，如果验证失败则返回null
     */
    public static Claims validateJWT(String token) {
        try {
            Jws<Claims> claimsJws = JWTToken.parseClaim(token);
            if (JWTToken.isTokenExpired(token)) {
                logError("JWT已过期: " + token, null, "Utils");
                return null;
            }
            return claimsJws.getPayload();
        } catch (JwtException e) {
            logError("JWT验证失败: " + token, e, "Utils");
            return null;
        }
    }

    /**
     * 日志级别枚举类
     */
    public enum LogLevel {
        ERROR, WARN, INFO, DEBUG, TRACE
    }

    public static void log(LogLevel level, String message, Throwable throwable, String context) {
        switch (level) {
            case ERROR:
                logger.error(buildLogMessage(message, context), throwable);
                break;
            case WARN:
                logger.warn(buildLogMessage(message, context), throwable);
                break;
            case INFO:
                logger.info(buildLogMessage(message, context), throwable);
                break;
            case DEBUG:
                logger.debug(buildLogMessage(message, context), throwable);
                break;
            case TRACE:
                logger.trace(buildLogMessage(message, context), throwable);
                break;
            default:
                logger.info(buildLogMessage(message, context), throwable); // 默认使用 INFO 级别
                break;
        }
    }

    /**
     * 记录错误日志的快捷方法
     *
     * @param message   日志信息
     * @param throwable 异常对象（可以为 null）
     * @param context   额外的上下文信息（可以为 null）
     */
    public static void logError(String message, Throwable throwable, String context) {
        log(LogLevel.ERROR, message, throwable, context);
    }

    /**
     * 构建日志信息，包含上下文
     *
     * @param message 日志信息
     * @param context 上下文信息
     * @return 完整的日志信息
     */
    private static String buildLogMessage(String message, String context) {
        if (context != null && !context.isEmpty()) {
            return String.format("[Context: %s] %s", context, message);
        }
        return message;
    }
}