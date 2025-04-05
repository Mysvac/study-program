package com.asaki0019.advertising.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecureDigestAlgorithm;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTToken {
    /**
     * 加密算法
     */
    private final static SecureDigestAlgorithm<SecretKey, SecretKey> ALGORITHM = Jwts.SIG.HS256;
    /**
     * 私钥 / 生成签名的时候使用的秘钥secret，一般可以从本地配置文件中读取，切记这个秘钥不能外露，只在服务端使用，在任何场景都不应该流露出去。
     * 一旦客户端得知这个secret, 那就意味着客户端是可以自我签发jwt了。
     * 应该大于等于 256位(长度32及以上的字符串)，并且是随机的字符串
     */
    private final static String SECRET = "SecretKey_Asaki0019_Password_JWT_Code";
    /**
     * 秘钥实例
     */
    public static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET.getBytes());

    // JWT的有效期，这里设置为1小时
    private static final long EXPIRATION_TIME = 3600 * 1000; // 1 hour
    /**
     * jwt签发者
     */
    private final static String JWT_ISS = "Asaki0019";
    /**
     * jwt主题
     */
    private final static String SUBJECT = "Peripherals";

    /**
     * 生成JWT
     * <p>
     * 该方法用于生成一个包含用户名信息的JWT。
     * JWT（JSON Web Token）是一种开放标准（RFC 7519），用于在网络应用环境间安全地将信息作为JSON对象传输。
     * 生成的JWT包含以下信息：
     * - claims: 包含用户名的声明
     * - subject: 用户名
     * - issued at: JWT的签发时间
     * - expiration: JWT的过期时间
     * - signature: 用于验证JWT完整性和来源的签名
     *
     * @param username 用户名
     * @return 生成的JWT字符串
     */
    public static String generateToken(String username, String uuid) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("username", username);
            claims.put("uuid", uuid); // 将 uuid 添加到 claims 中
            return Jwts.builder()
                    .header()
                    .add("typ", "JWT")
                    .add("alg", "HS256")
                    .and()
                    .claims(claims)
                    .subject(username)
                    .issuedAt(new Date())
                    .issuer(JWT_ISS)
                    .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .signWith(KEY, ALGORITHM)
                    .compact();
        } catch (Exception e) {
            Utils.logError("JWT 签发失败", e, "JWT未知错误");
            throw e;
        }
    }


    /**
     * 解析JWT并获取Claims
     *
     * @param token JWT字符串
     * @return 解析后的Claims
     */
    public static Jws<Claims> parseClaim(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(KEY)
                    .build()
                    .parseSignedClaims(token);
        } catch (Exception e) {
            Utils.logError("JWT解析失败", e, "JWT未知错误");
            throw e;
        }
    }
    /**
     * 解析JWT并获取Header
     *
     * @param token JWT字符串
     * @return 解析后的Header
     */
    public static JwsHeader parseHeader(String token) {
        try {
            return parseClaim(token).getHeader();
        }catch (Exception e){
            Utils.logError("JWT解析失败", e, "JWT未知错误");
            throw e;
        }
    }

    /**
     * 解析JWT并获取Payload
     *
     * @param token JWT字符串
     * @return 解析后的Payload
     */
    public static Claims parsePayload(String token) {
        return parseClaim(token).getPayload();
    }

    /**
     * 判断JWT是否过期
     *
     * @param token JWT字符串
     * @return 是否过期
     */
    public static boolean isTokenExpired(String token) {
        Claims claims = parsePayload(token);
        return claims.getExpiration().before(new Date()); // 检查过期时间是否在当前时间之前
    }
}
