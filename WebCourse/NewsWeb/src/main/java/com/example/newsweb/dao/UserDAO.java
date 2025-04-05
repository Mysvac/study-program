package com.example.newsweb.dao;

import com.example.newsweb.model.User;

import java.util.List;

public interface UserDAO extends DAO{
    public boolean validateUser(String username, String password);
    public List<User> getAllUsers();
    public boolean isUserRegistered(String username);
    public boolean registerUser(User user);
}
