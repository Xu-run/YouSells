package com.yousells.modules.customer;

import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.common.security.LoginUser;
import com.yousells.modules.auth.entity.UserEntity;
import com.yousells.modules.auth.mapper.UserMapper;
import com.yousells.modules.customer.dto.CustomerCreateRequest;
import com.yousells.modules.customer.dto.CustomerQueryRequest;
import com.yousells.modules.customer.dto.CustomerUpdateRequest;
import com.yousells.modules.customer.service.CustomerService;
import com.yousells.modules.customer.vo.CustomerDetailVo;
import com.yousells.modules.customer.vo.CustomerListItemVo;
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
class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private UserMapper userMapper;

    private Long userId1;
    private Long userId2;
    private Long customerId1;
    private Long customerId2;

    @BeforeEach
    void setUp() {
        UserEntity u1 = new UserEntity();
        u1.setUsername("admin");
        u1.setPasswordHash("hash");
        u1.setRealName("秦梓源");
        u1.setLevel("T2");
        u1.setStatus("ACTIVE");
        userMapper.insert(u1);
        userId1 = u1.getId();

        UserEntity u2 = new UserEntity();
        u2.setUsername("member");
        u2.setPasswordHash("hash");
        u2.setRealName("小赵");
        u2.setLevel("T0");
        u2.setManagerUserId(userId1);
        u2.setStatus("ACTIVE");
        userMapper.insert(u2);
        userId2 = u2.getId();

        LoginUser loginUser = new LoginUser(userId1, "admin", "秦梓源", "T2", null);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(loginUser, null, List.of()));

        CustomerCreateRequest req1 = new CustomerCreateRequest(
                "王同学", "大一", "计算机科学", "计科2403", userId2, userId1,
                "技术栈", "可跟", "校园地推来的");
        customerId1 = customerService.createCustomer(req1);

        CustomerCreateRequest req2 = new CustomerCreateRequest(
                "张同学", "大二", "软件工程", "软工2301", userId1, userId1,
                "课程", "很稳", "知乎私信来的");
        customerId2 = customerService.createCustomer(req2);
    }

    // ── pageCustomers ──

    @Test
    void shouldPageAllCustomers() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, null, null, 1, 10));
        assertThat(result.total()).isEqualTo(2);
        assertThat(result.list()).hasSize(2);
    }

    @Test
    void shouldPaginateCorrectly() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, null, null, 1, 1));
        assertThat(result.list()).hasSize(1);
        assertThat(result.total()).isEqualTo(2);
    }

    @Test
    void shouldFilterByGrade() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, "大一", null, null, null, null, 1, 10));
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.list().get(0).grade()).isEqualTo("大一");
    }

    @Test
    void shouldFilterByProgress() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, "技术栈", null, null, 1, 10));
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.list().get(0).progress()).isEqualTo("技术栈");
    }

    @Test
    void shouldFilterByIntent() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, "很稳", null, 1, 10));
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.list().get(0).intent()).isEqualTo("很稳");
    }

    @Test
    void shouldFilterByOwnerUserId() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, null, userId1, 1, 10));
        assertThat(result.total()).isGreaterThanOrEqualTo(1);
        result.list().forEach(c -> assertThat(c.ownerDisplayName()).isEqualTo("秦梓源"));
    }

    @Test
    void shouldSearchByKeyword() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest("王同", null, null, null, null, null, 1, 10));
        assertThat(result.total()).isEqualTo(1);
        assertThat(result.list().get(0).realName()).isEqualTo("王同学");
    }

    @Test
    void shouldReturnEmptyForNoMatch() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest("不存在", null, null, null, null, null, 1, 10));
        assertThat(result.total()).isEqualTo(0);
        assertThat(result.list()).isEmpty();
    }

    @Test
    void shouldIncludeDisplayNamesInList() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, null, null, 1, 10));
        CustomerListItemVo first = result.list().get(0);
        assertThat(first.ownerDisplayName()).isNotBlank();
        assertThat(first.inviterDisplayName()).isNotBlank();
    }

    @Test
    void shouldIncludeCreatedAtInList() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, null, null, 1, 10));
        assertThat(result.list().get(0).createdAt()).isNotNull();
    }

    // ── getCustomerDetail ──

    @Test
    void shouldGetCustomerDetail() {
        CustomerDetailVo detail = customerService.getCustomerDetail(customerId1);
        assertThat(detail.id()).isEqualTo(customerId1);
        assertThat(detail.realName()).isEqualTo("王同学");
        assertThat(detail.grade()).isEqualTo("大一");
        assertThat(detail.major()).isEqualTo("计算机科学");
        assertThat(detail.className()).isEqualTo("计科2403");
        assertThat(detail.progress()).isEqualTo("技术栈");
        assertThat(detail.intent()).isEqualTo("可跟");
        assertThat(detail.inviterNote()).isEqualTo("校园地推来的");
        assertThat(detail.ownerDisplayName()).isEqualTo("秦梓源");
        assertThat(detail.inviterDisplayName()).isEqualTo("小赵");
    }

    @Test
    void shouldThrowNotFoundWhenCustomerNotExists() {
        assertThatThrownBy(() -> customerService.getCustomerDetail(9999L))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.NOT_FOUND);
    }

    // ── createCustomer ──

    @Test
    void shouldCreateCustomer() {
        CustomerCreateRequest request = new CustomerCreateRequest(
                "李同学", "大一", "数学应用", "数学2401", userId2, userId1,
                "职规", "观望", "抖音来的");
        Long newId = customerService.createCustomer(request);
        assertThat(newId).isNotNull().isPositive();

        CustomerDetailVo created = customerService.getCustomerDetail(newId);
        assertThat(created.realName()).isEqualTo("李同学");
        assertThat(created.progress()).isEqualTo("职规");
        assertThat(created.intent()).isEqualTo("观望");
    }

    @Test
    void shouldDefaultProgressAndIntent() {
        CustomerCreateRequest request = new CustomerCreateRequest(
                "赵同学", "大二", "通信工程", null, userId1, userId2, null, null, null);
        Long newId = customerService.createCustomer(request);

        CustomerDetailVo created = customerService.getCustomerDetail(newId);
        assertThat(created.progress()).isEqualTo("职规");
        assertThat(created.intent()).isEqualTo("观望");
    }

    // ── updateCustomer ──

    @Test
    void shouldUpdateCustomer() {
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "王同学改", "大二", "数据科学", "计科2403改",
                userId2, userId1, "课程", "很稳", "更新备注");
        customerService.updateCustomer(customerId1, request);

        CustomerDetailVo updated = customerService.getCustomerDetail(customerId1);
        assertThat(updated.realName()).isEqualTo("王同学改");
        assertThat(updated.grade()).isEqualTo("大二");
        assertThat(updated.major()).isEqualTo("数据科学");
        assertThat(updated.progress()).isEqualTo("课程");
        assertThat(updated.intent()).isEqualTo("很稳");
        assertThat(updated.inviterNote()).isEqualTo("更新备注");
    }

    @Test
    void shouldThrowNotFoundWhenUpdatingNonExistentCustomer() {
        CustomerUpdateRequest request = new CustomerUpdateRequest(
                "某某", "大一", "测试", null, userId1, userId1, "职规", "观望", null);
        assertThatThrownBy(() -> customerService.updateCustomer(9999L, request))
                .isInstanceOf(BusinessException.class)
                .extracting("code")
                .isEqualTo(ErrorCodeConstants.NOT_FOUND);
    }

    // ── data scope ──

    @Test
    void shouldShowCustomersInScopeForT2() {
        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, null, null, 1, 10));
        assertThat(result.total()).isEqualTo(2);
    }

    @Test
    void shouldShowOnlyOwnCustomersForT0() {
        LoginUser t0User = new LoginUser(userId2, "member", "小赵", "T0", userId1);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(t0User, null, List.of()));

        PageResponse<CustomerListItemVo> result = customerService.pageCustomers(
                new CustomerQueryRequest(null, null, null, null, null, null, 1, 10));
        assertThat(result.total()).isEqualTo(1);
        // T0 can see own inviter+owner customers
    }
}
