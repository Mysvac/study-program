package com.asaki0019.advertising.controller;

import com.asaki0019.advertising.model.User;
import com.asaki0019.advertising.service.UserService;
import com.asaki0019.advertising.utils.JWTToken;
import com.asaki0019.advertising.utils.Utils;
import jakarta.servlet.http.HttpSession;
import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 处理用户注册、登录和退出请求的控制器。
 */
@RestController
@RequestMapping("/api")
public class UserAuthenticationController {

    private final UserService userService;

    /**
     * 构造函数，使用构造函数注入依赖。
     *
     * @param userService 用户服务
     */
    public UserAuthenticationController(UserService userService) {
        this.userService = userService;
    }

    /**
     * 用户注册接口。
     *
     * @param body 包含用户注册信息的请求体
     * @return 包含注册结果的响应实体
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        try {
            String username = body.get("username");
            String name = body.get("name");
            String password = body.get("password");
            String verifiedPassword = body.get("verifiedPassword");

            // 验证参数
            if (username == null || name == null || password == null || !password.equals(verifiedPassword)) {
                Utils.log(Utils.LogLevel.INFO, "用户名、密码和确认密码为空或不匹配", null, "UserAuthenticationController");
                return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "用户名、密码和确认密码不能为空或不匹配"));
            }

            // 创建用户对象
            User user = new User();
            user.setUsername(username);
            user.setName(name);
            user.setPassword(password);
            user.setRole("common");

            // 调用服务层注册用户
            boolean result = userService.registerUser(user);
            if (result) {
                return ResponseEntity.ok(Map.of("code", 200, "message", "注册成功", "data", user));
            } else {
                return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "用户已存在"));
            }
        } catch (Exception e) {
            Utils.logError("用户注册处理失败", e, "UserAuthenticationController");
            return ResponseEntity.status(500).body(Map.of("code", 500, "message", e.getMessage()));
        }
    }

    /**
     * 用户登录接口。
     *
     * @param body    包含用户登录信息的请求体
     * @param session HTTP 会话对象
     * @return 包含登录结果的响应实体
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpSession session) {
        try {
            String username = body.get("username");
            String password = body.get("password");

            // 验证参数
            if (username == null || password == null) {
                Utils.log(Utils.LogLevel.INFO, "用户名或密码为空", null, "UserAuthenticationController");
                return ResponseEntity.badRequest().body(Map.of("code", 400, "message", "用户名和密码不能为空"));
            }

            // 调用服务层登录用户
            User user = userService.loginUser(username, password);
            if (user == null) {
                return ResponseEntity.status(401).body(Map.of("code", 401, "message", "用户不存在"));
            }
            var jwt = JWTToken.generateToken(username, user.getId());
            Map<String, Object> data = new HashMap<>();
            data.put("jwt", jwt);
            data.put("name", user.getName());
            data.put("cookie", user.getId()); // 模拟 Cookie
            data.put("role", user.getRole()); // 模拟角色
            return ResponseEntity.ok(Map.of("code", 200, "message", "登录成功", "data", data));

        } catch (Exception e) {
            Utils.logError("用户登录处理失败", e, "UserAuthenticationController");
        }
        return ResponseEntity.status(401).body(Map.of("code", 401, "message", "用户名或密码错误"));
    }

    /**
     * 用户退出接口。
     *
     * @return 包含退出结果的响应实体
     */
    @GetMapping("/exit")
    public ResponseEntity<Map<String, Object>> exitUser() {
        return ResponseEntity.ok(Map.of("code", 200, "message", "退出登录成功"));
    }
}