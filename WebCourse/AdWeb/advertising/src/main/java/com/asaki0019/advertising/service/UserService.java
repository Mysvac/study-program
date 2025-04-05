package com.asaki0019.advertising.service;

import com.asaki0019.advertising.model.User;

public interface UserService {
    boolean registerUser(User user);
    User loginUser(String username, String password);
}
