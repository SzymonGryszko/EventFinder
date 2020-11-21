package com.gryszko.eventFinder.controllers;

import com.gryszko.eventFinder.dto.UserDto;
import com.gryszko.eventFinder.exception.NotFoundException;
import com.gryszko.eventFinder.service.AdminService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@AllArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @DeleteMapping("/{id}")
    public void deleteUser(Long userId) {
        adminService.deleteUser(userId);
    }

    @PutMapping("/{id}")
    public void updateUserRole(@PathVariable Long id, Integer role) throws NotFoundException {
        adminService.updateUserRole(id, role);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return adminService.getAllUsers();
    }

}
