package com.asaki0019.advertising.service.impl;

import com.asaki0019.advertising.mapper.UserMapper;
import com.asaki0019.advertising.model.User;
import com.asaki0019.advertising.service.UserService;
import com.asaki0019.advertising.utils.Utils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Autowired
    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public boolean registerUser(User user) {
        try {
            // 检查用户是否已存在
            User existingUser = userMapper.selectOne(
                    new QueryWrapper<User>().eq("username", user.getUsername())
            );
            if (existingUser != null) {
                Utils.log(Utils.LogLevel.WARN, "用户已存在", null, "username: " + user.getUsername());
                return false; // 用户已存在
            }

            // 插入新用户
            int result = userMapper.insert(user);
            if (result > 0) {
                Utils.log(Utils.LogLevel.INFO, "用户注册成功", null, "username: " + user.getUsername());
                return true;
            } else {
                Utils.logError("用户注册失败", null, "username: " + user.getUsername());
                return false;
            }
        } catch (RuntimeException e) {
            Utils.logError("用户注册失败", e, "username: " + user.getUsername());
            throw e; // 重新抛出异常
        }
    }

    @Override
    public User loginUser(String username, String password) {
        try {
            // 查询用户
            User user = userMapper.selectOne(
                    new QueryWrapper<User>().eq("username", username).eq("password", password)
            );
            if (user != null) {
                Utils.log(Utils.LogLevel.INFO, "用户登录成功", null, "username: " + username);
            } else {
                Utils.log(Utils.LogLevel.WARN, "用户登录失败：用户名或密码错误", null, "username: " + username);
            }
            return user;
        } catch (RuntimeException e) {
            Utils.logError("用户登录失败", e, "username: " + username);
            throw e; // 重新抛出异常
        }
    }
}