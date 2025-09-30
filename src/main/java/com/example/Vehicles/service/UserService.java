package com.example.Vehicles.service;

import com.example.Vehicles.dto.UserRequest;
import com.example.Vehicles.model.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void register(UserRequest req);
    Optional<User> findByLogin(String login);

    void softDeleteUser(String userId);

    List<User> getAllUsers();

    void addRoleToUser(String userId, String roleName);

    void removeRoleFromUser(String userId, String roleName);
}
