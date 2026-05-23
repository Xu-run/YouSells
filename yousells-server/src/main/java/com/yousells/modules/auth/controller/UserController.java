package com.yousells.modules.auth.controller;

import com.yousells.common.response.ApiResponse;
import com.yousells.common.security.LoginUser;
import com.yousells.common.security.SecurityUserContext;
import com.yousells.modules.auth.dto.CreateUserRequest;
import com.yousells.modules.auth.dto.ResignUserRequest;
import com.yousells.modules.auth.dto.UpdatePasswordRequest;
import com.yousells.modules.auth.dto.UpdateProfileRequest;
import com.yousells.modules.auth.dto.UpdateUserRequest;
import com.yousells.modules.auth.service.UserService;
import com.yousells.modules.auth.vo.UserListItemVo;
import com.yousells.modules.auth.vo.UserProfileVo;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<UserListItemVo>> listUsers() {
        return ApiResponse.success(userService.listUsers());
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileVo> getCurrentUserProfile() {
        LoginUser currentUser = SecurityUserContext.requireCurrentUser();
        return ApiResponse.success(userService.getUser(currentUser.userId()));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileVo> updateCurrentUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
        LoginUser currentUser = SecurityUserContext.requireCurrentUser();
        return ApiResponse.success(userService.updateProfile(currentUser.userId(), request));
    }

    @PutMapping("/me/password")
    public ApiResponse<Void> updateCurrentUserPassword(@Valid @RequestBody UpdatePasswordRequest request) {
        LoginUser currentUser = SecurityUserContext.requireCurrentUser();
        userService.updatePassword(currentUser.userId(), request);
        return ApiResponse.success();
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserProfileVo> getUser(@PathVariable Long userId) {
        return ApiResponse.success(userService.getUser(userId));
    }

    @PostMapping
    public ApiResponse<Long> createUser(@Valid @RequestBody CreateUserRequest request) {
        return ApiResponse.success(userService.createUser(request));
    }

    @PutMapping("/{userId}")
    public ApiResponse<Void> updateUser(@PathVariable Long userId, @Valid @RequestBody UpdateUserRequest request) {
        userService.updateUser(userId, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{userId}/resign")
    public ApiResponse<Void> resignUser(@PathVariable Long userId, @Valid @RequestBody ResignUserRequest request) {
        userService.resignUser(userId, request);
        return ApiResponse.success();
    }
}
