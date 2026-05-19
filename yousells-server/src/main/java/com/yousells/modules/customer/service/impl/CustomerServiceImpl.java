package com.yousells.modules.customer.service.impl;

import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.modules.customer.dto.CustomerCreateRequest;
import com.yousells.modules.customer.dto.CustomerNextFollowRequest;
import com.yousells.modules.customer.dto.CustomerQueryRequest;
import com.yousells.modules.customer.dto.CustomerTagsUpdateRequest;
import com.yousells.modules.customer.dto.CustomerUpdateRequest;
import com.yousells.modules.customer.service.CustomerService;
import com.yousells.modules.customer.vo.CustomerDetailVo;
import com.yousells.modules.customer.vo.CustomerListItemVo;
import com.yousells.modules.customer.vo.CustomerTagVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final List<CustomerTagVo> sampleTags = List.of(
            new CustomerTagVo(1L, "竞赛意向", "DIRECTION", "#2563eb"),
            new CustomerTagVo(2L, "零基础", "BASE", "#16a34a"),
            new CustomerTagVo(3L, "高意向", "ACTIVITY", "#dc2626")
    );

    private final List<CustomerDetailVo> sampleCustomers = List.of(
            new CustomerDetailVo(
                    1001L,
                    "C20260518001",
                    "OLD_GRADE1",
                    "小林",
                    "QQ: 10001",
                    "QQ",
                    "计算机科学与技术",
                    "C 语言入门",
                    "Web / 前端",
                    "A",
                    "HIGH_INTENT",
                    "担心能否坚持学完",
                    "对项目和答疑很感兴趣",
                    LocalDateTime.of(2026, 5, 17, 21, 30),
                    "发 3 天学习体验路径",
                    LocalDateTime.of(2026, 5, 19, 12, 0),
                    "秦梓源",
                    "志明",
                    List.of("竞赛意向", "高意向"),
                    "建议 6 月中旬前完成成交判断"
            ),
            new CustomerDetailVo(
                    1002L,
                    "C20260518002",
                    "OLD_GRADE1",
                    "阿泽",
                    "微信: az-2026",
                    "微信",
                    "软件工程",
                    "Python 入门",
                    "Agent / 自动化",
                    "B",
                    "NURTURING",
                    "还在观望价格",
                    "希望先多看看项目案例",
                    LocalDateTime.of(2026, 5, 18, 10, 0),
                    "安排一次项目案例讲解",
                    LocalDateTime.of(2026, 5, 20, 20, 0),
                    "志明",
                    "哲涛",
                    List.of("零基础"),
                    "继续养熟"
            ),
            new CustomerDetailVo(
                    1003L,
                    "C20260518003",
                    "OLD_GRADE1",
                    "阿宁",
                    "QQ: 20003",
                    "QQ",
                    "数据科学",
                    "零基础",
                    "竞赛 / 项目",
                    "C",
                    "FIRST_COMMUNICATION",
                    "还不了解课程结构",
                    "愿意进技术交流群",
                    LocalDateTime.of(2026, 5, 16, 19, 45),
                    "邀请进群并发群文件",
                    LocalDateTime.of(2026, 5, 21, 18, 0),
                    "哲涛",
                    null,
                    List.of("竞赛意向", "零基础"),
                    "适合慢慢培养"
            )
    );

    @Override
    public PageResponse<CustomerListItemVo> pageCustomers(CustomerQueryRequest request) {
        int page = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 20 : request.pageSize();

        List<CustomerListItemVo> filtered = sampleCustomers.stream()
                .filter(customer -> matchKeyword(customer, request.keyword()))
                .filter(customer -> request.intentLevel() == null || request.intentLevel().isBlank() || request.intentLevel().equals(customer.intentLevel()))
                .filter(customer -> request.currentStage() == null || request.currentStage().isBlank() || request.currentStage().equals(customer.currentStage()))
                .filter(customer -> request.sourcePlatform() == null || request.sourcePlatform().isBlank() || request.sourcePlatform().equals(customer.sourcePlatform()))
                .map(customer -> new CustomerListItemVo(
                        customer.id(),
                        customer.customerCode(),
                        customer.nickname(),
                        customer.customerType(),
                        customer.sourcePlatform(),
                        customer.intentLevel(),
                        customer.currentStage(),
                        customer.ownerDisplayName(),
                        customer.lastContactAt(),
                        customer.nextFollowAt(),
                        customer.tags()
                ))
                .toList();

        int fromIndex = Math.min((page - 1) * pageSize, filtered.size());
        int toIndex = Math.min(fromIndex + pageSize, filtered.size());
        return PageResponse.of(filtered.subList(fromIndex, toIndex), page, pageSize, filtered.size());
    }

    @Override
    public CustomerDetailVo getCustomerDetail(Long id) {
        return sampleCustomers.stream()
                .filter(customer -> customer.id().equals(id))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCodeConstants.NOT_FOUND, "customer not found"));
    }

    @Override
    public Long createCustomer(CustomerCreateRequest request) {
        return 9000L;
    }

    @Override
    public void updateCustomer(Long id, CustomerUpdateRequest request) {
        getCustomerDetail(id);
    }

    @Override
    public List<CustomerTagVo> listTags() {
        return sampleTags;
    }

    @Override
    public void updateTags(Long id, CustomerTagsUpdateRequest request) {
        getCustomerDetail(id);
    }

    @Override
    public void updateNextFollow(Long id, CustomerNextFollowRequest request) {
        getCustomerDetail(id);
    }

    private boolean matchKeyword(CustomerDetailVo customer, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        return customer.nickname().contains(keyword)
                || customer.customerCode().contains(keyword)
                || customer.contactValue().contains(keyword);
    }
}
