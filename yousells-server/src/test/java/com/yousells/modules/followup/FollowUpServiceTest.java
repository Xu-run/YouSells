package com.yousells.modules.followup;

import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.common.security.LoginUser;
import com.yousells.modules.customer.entity.CustomerEntity;
import com.yousells.modules.customer.mapper.CustomerMapper;
import com.yousells.modules.followup.dto.FollowUpCreateRequest;
import com.yousells.modules.followup.dto.FollowUpQueryRequest;
import com.yousells.modules.followup.service.FollowUpService;
import com.yousells.modules.followup.vo.FollowUpVo;
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
class FollowUpServiceTest {

    @Autowired
    private FollowUpService followUpService;

    @Autowired
    private CustomerMapper customerMapper;

    private Long customerId;

    @BeforeEach
    void setUp() {
        LoginUser loginUser = new LoginUser(1L, "admin", "秦梓源", "T2", null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, List.of()));

        CustomerEntity c = new CustomerEntity();
        c.setRealName("王同学");
        c.setGrade("大一");
        c.setMajor("计算机科学");
        c.setClassName("计科2403");
        c.setInviterUserId(1L);
        c.setOwnerUserId(1L);
        c.setProgress("职规");
        c.setIntent("可跟");
        customerMapper.insert(c);
        customerId = c.getId();
    }

    @Test
    void shouldCreateFollowUp() {
        FollowUpCreateRequest request = new FollowUpCreateRequest(
                customerId, "职规", "第一次沟通内容", "学生说想了解蓝桥杯", "发蓝桥杯资料");
        Long id = followUpService.createFollowUp(request);
        assertThat(id).isNotNull().isPositive();
    }

    @Test
    void shouldPageFollowUpsByCustomerId() {
        FollowUpCreateRequest req1 = new FollowUpCreateRequest(
                customerId, "职规", "第一次沟通", "感兴趣", "发资料");
        followUpService.createFollowUp(req1);

        FollowUpCreateRequest req2 = new FollowUpCreateRequest(
                customerId, "技术栈", "第二次沟通", "确定方向", "发课程方案");
        followUpService.createFollowUp(req2);

        PageResponse<FollowUpVo> result = followUpService.pageFollowUps(
                new FollowUpQueryRequest(customerId, 1, 10));
        assertThat(result.total()).isEqualTo(2);
        assertThat(result.list()).hasSize(2);
        assertThat(result.list().get(0).createdAt())
                .isAfterOrEqualTo(result.list().get(1).createdAt());
    }

    @Test
    void shouldReturnEmptyForCustomerWithoutFollowUps() {
        PageResponse<FollowUpVo> result = followUpService.pageFollowUps(
                new FollowUpQueryRequest(9999L, 1, 10));
        assertThat(result.total()).isEqualTo(0);
        assertThat(result.list()).isEmpty();
    }

    @Test
    void shouldIncludeAllFieldsInVo() {
        FollowUpCreateRequest request = new FollowUpCreateRequest(
                customerId, "课程", "最终沟通", "很满意，决定报名", "安排进课程群");
        Long id = followUpService.createFollowUp(request);

        PageResponse<FollowUpVo> result = followUpService.pageFollowUps(
                new FollowUpQueryRequest(customerId, 1, 10));
        FollowUpVo vo = result.list().get(0);
        assertThat(vo.id()).isEqualTo(id);
        assertThat(vo.customerId()).isEqualTo(customerId);
        assertThat(vo.progress()).isEqualTo("课程");
        assertThat(vo.content()).isEqualTo("最终沟通");
        assertThat(vo.feedback()).isEqualTo("很满意，决定报名");
        assertThat(vo.nextAction()).isEqualTo("安排进课程群");
        assertThat(vo.createdAt()).isNotNull();
    }

    @Test
    void shouldThrowNotFoundWhenCreatingForNonExistentCustomer() {
        FollowUpCreateRequest request = new FollowUpCreateRequest(
                9999L, "职规", "内容", "反馈", "下一步");
        assertThatThrownBy(() -> followUpService.createFollowUp(request))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.NOT_FOUND);
    }
}
