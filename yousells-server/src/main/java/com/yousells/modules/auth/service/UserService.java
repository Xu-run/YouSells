package com.yousells.modules.auth.service;

import com.yousells.modules.auth.dto.CreateUserRequest;
import com.yousells.modules.auth.dto.ResignUserRequest;
import com.yousells.modules.auth.dto.UpdatePasswordRequest;
import com.yousells.modules.auth.dto.UpdateProfileRequest;
import com.yousells.modules.auth.dto.UpdateUserRequest;
import com.yousells.modules.auth.vo.UserListItemVo;
import com.yousells.modules.auth.vo.UserProfileVo;

import java.util.List;

public interface UserService {

    List<UserListItemVo> listUsers();

    UserProfileVo getUser(Long userId);

    UserProfileVo updateProfile(Long userId, UpdateProfileRequest request);

    void updatePassword(Long userId, UpdatePasswordRequest request);

    Long createUser(CreateUserRequest request);

    void updateUser(Long userId, UpdateUserRequest request);

    void resignUser(Long userId, ResignUserRequest request);
}
