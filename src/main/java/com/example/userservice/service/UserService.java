package com.example.userservice.service;

import com.example.userservice.dto.UserDto;
import com.example.userservice.entity.User;

public interface UserService {

    void createUser(UserDto userDto);

    UserDto getUserById(String userId);

    Iterable<User> getUserByAll();
}
