package com.example.Vehicles.controller;

import com.example.Vehicles.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserService userService;

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok("User deactivated");
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping("/users/{id}/roles/{role}")
    public ResponseEntity<?> addRole(@PathVariable String id, @PathVariable String role) {
        userService.addRoleToUser(id, role);
        return ResponseEntity.ok("Role added");
    }

    @DeleteMapping("/users/{id}/roles/{role}")
    public ResponseEntity<?> removeRole(@PathVariable String id, @PathVariable String role) {
        userService.removeRoleFromUser(id, role);
        return ResponseEntity.ok("Role removed");
    }
}

