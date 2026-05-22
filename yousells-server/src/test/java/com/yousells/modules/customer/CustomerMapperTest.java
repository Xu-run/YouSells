package com.yousells.modules.customer;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.modules.customer.entity.CustomerEntity;
import com.yousells.modules.customer.mapper.CustomerMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
@Transactional
class CustomerMapperTest {

    @Autowired
    private CustomerMapper customerMapper;

    private Long customerId1;
    private Long customerId2;

    @BeforeEach
    void setUp() {
        CustomerEntity c1 = new CustomerEntity();
        c1.setRealName("王同学");
        c1.setGrade("大一");
        c1.setMajor("计算机科学");
        c1.setClassName("计科2403");
        c1.setInviterUserId(2L);
        c1.setOwnerUserId(1L);
        c1.setProgress("技术栈");
        c1.setIntent("可跟");
        c1.setInviterNote("地推来的");
        customerMapper.insert(c1);
        customerId1 = c1.getId();

        CustomerEntity c2 = new CustomerEntity();
        c2.setRealName("张同学");
        c2.setGrade("大二");
        c2.setMajor("软件工程");
        c2.setClassName("软工2301");
        c2.setInviterUserId(1L);
        c2.setOwnerUserId(1L);
        c2.setProgress("课程");
        c2.setIntent("很稳");
        c2.setInviterNote("知乎来的");
        customerMapper.insert(c2);
        customerId2 = c2.getId();
    }

    @Test
    void shouldPageAllCustomers() {
        Page<CustomerEntity> page = new Page<>(1, 10);
        IPage<CustomerEntity> result = customerMapper.pageCustomers(page,
                null, null, null, null, null, null, List.of(1L, 2L));
        assertThat(result.getTotal()).isEqualTo(2);
        assertThat(result.getRecords()).hasSize(2);
    }

    @Test
    void shouldFilterByGrade() {
        Page<CustomerEntity> page = new Page<>(1, 10);
        IPage<CustomerEntity> result = customerMapper.pageCustomers(page,
                null, "大一", null, null, null, null, List.of(1L, 2L));
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords().get(0).getRealName()).isEqualTo("王同学");
    }

    @Test
    void shouldFilterByProgress() {
        Page<CustomerEntity> page = new Page<>(1, 10);
        IPage<CustomerEntity> result = customerMapper.pageCustomers(page,
                null, null, null, "课程", null, null, List.of(1L, 2L));
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords().get(0).getProgress()).isEqualTo("课程");
    }

    @Test
    void shouldFilterByIntent() {
        Page<CustomerEntity> page = new Page<>(1, 10);
        IPage<CustomerEntity> result = customerMapper.pageCustomers(page,
                null, null, null, null, "很稳", null, List.of(1L, 2L));
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords().get(0).getIntent()).isEqualTo("很稳");
    }

    @Test
    void shouldSearchByKeyword() {
        Page<CustomerEntity> page = new Page<>(1, 10);
        IPage<CustomerEntity> result = customerMapper.pageCustomers(page,
                "张同学", null, null, null, null, null, List.of(1L, 2L));
        assertThat(result.getTotal()).isEqualTo(1);
    }

    @Test
    void shouldFilterByVisibleUserIds() {
        Page<CustomerEntity> page = new Page<>(1, 10);
        IPage<CustomerEntity> result = customerMapper.pageCustomers(page,
                null, null, null, null, null, null, List.of(2L));
        assertThat(result.getTotal()).isEqualTo(1);
        assertThat(result.getRecords().get(0).getInviterUserId()).isEqualTo(2L);
    }
}
