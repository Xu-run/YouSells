package com.yousells.modules.auth;

import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.security.DataScopeHelper;
import com.yousells.common.security.LoginUser;
import com.yousells.modules.auth.dto.UpdateProfileRequest;
import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;
import com.yousells.modules.auth.service.UserService;
import com.yousells.modules.auth.vo.UserProfileVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserMapper userMapper;

    private Long t3UserId;
    private Long t2UserId;
    private Long t0UserId;

    @BeforeEach
    void setUp() {
        DataScopeHelper.invalidateAllCache();
        UserEntity t3 = new UserEntity();
        t3.setUsername("boss");
        t3.setPasswordHash("hash");
        t3.setRealName("老板");
        t3.setLevel("T3");
        t3.setStatus("ACTIVE");
        userMapper.insert(t3);
        t3UserId = t3.getId();

        UserEntity t2 = new UserEntity();
        t2.setUsername("admin");
        t2.setPasswordHash("hash");
        t2.setRealName("秦梓源");
        t2.setLevel("T2");
        t2.setManagerUserId(t3UserId);
        t2.setStatus("ACTIVE");
        userMapper.insert(t2);
        t2UserId = t2.getId();

        UserEntity t0 = new UserEntity();
        t0.setUsername("member");
        t0.setPasswordHash("hash");
        t0.setRealName("小赵");
        t0.setLevel("T0");
        t0.setManagerUserId(t2UserId);
        t0.setStatus("ACTIVE");
        userMapper.insert(t0);
        t0UserId = t0.getId();

        LoginUser loginUser = new LoginUser(t2UserId, "admin", "秦梓源", "T2", t3UserId);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, List.of()));
    }

    @Test
    void shouldAllowGetOwnProfile() {
        UserProfileVo profile = userService.getUser(t2UserId);
        assertThat(profile.username()).isEqualTo("admin");
    }

    @Test
    void shouldAllowGetSubordinateProfile() {
        UserProfileVo profile = userService.getUser(t0UserId);
        assertThat(profile.username()).isEqualTo("member");
    }

    @Test
    void shouldForbidGetNonSubordinateProfile() {
        assertThatThrownBy(() -> userService.getUser(t3UserId))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.FORBIDDEN);
    }

    @Test
    void shouldForbidCreateUserWhenNotT3() {
        assertThatThrownBy(() -> userService.createUser(
                new com.yousells.modules.auth.dto.CreateUserRequest(
                        "newbie", "pass", "新人", "T0", t2UserId)))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.FORBIDDEN);
    }

    @Test
    void shouldAllowCreateUserWhenT3() {
        LoginUser t3Login = new LoginUser(t3UserId, "boss", "老板", "T3", null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(t3Login, null, List.of()));

        Long newId = userService.createUser(
                new com.yousells.modules.auth.dto.CreateUserRequest(
                        "newbie", "pass", "新人", "T0", t3UserId));
        assertThat(newId).isNotNull().isPositive();
    }

    @Test
    void shouldForbidUpdateUserWhenNotT3() {
        assertThatThrownBy(() -> userService.updateUser(
                t0UserId,
                new com.yousells.modules.auth.dto.UpdateUserRequest(
                        "小赵改", "T0", t2UserId, null)))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.FORBIDDEN);
    }

    @Test
    void shouldForbidResignUserWhenNotT3() {
        assertThatThrownBy(() -> userService.resignUser(
                t0UserId, new com.yousells.modules.auth.dto.ResignUserRequest("测试离职")))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.FORBIDDEN);
    }

    @Test
    void shouldAllowUpdateProfileForSelf() {
        userService.updateProfile(t2UserId, new UpdateProfileRequest("秦梓源改"));
        UserProfileVo updated = userService.getUser(t2UserId);
        assertThat(updated.realName()).isEqualTo("秦梓源改");
    }
}
