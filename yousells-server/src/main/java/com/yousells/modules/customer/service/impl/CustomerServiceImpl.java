package com.yousells.modules.customer.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yousells.common.constant.ErrorCodeConstants;
import com.yousells.common.exception.BusinessException;
import com.yousells.common.response.PageResponse;
import com.yousells.common.security.DataScopeHelper;
import com.yousells.common.security.LoginUser;
import com.yousells.common.security.SecurityUserContext;
import com.yousells.modules.auth.mapper.UserMapper;
import com.yousells.modules.customer.convert.CustomerConvert;
import com.yousells.modules.customer.dto.CustomerCreateRequest;
import com.yousells.modules.customer.dto.CustomerQueryRequest;
import com.yousells.modules.customer.dto.CustomerUpdateRequest;
import com.yousells.modules.customer.entity.CustomerEntity;
import com.yousells.modules.customer.mapper.CustomerMapper;
import com.yousells.modules.customer.service.CustomerService;
import com.yousells.modules.customer.vo.CustomerDetailVo;
import com.yousells.modules.customer.vo.CustomerListItemVo;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;
    private final UserMapper userMapper;

    public CustomerServiceImpl(CustomerMapper customerMapper, UserMapper userMapper) {
        this.customerMapper = customerMapper;
        this.userMapper = userMapper;
    }

    @Override
    public PageResponse<CustomerListItemVo> pageCustomers(CustomerQueryRequest request) {
        int pageNum = request.page() == null || request.page() < 1 ? 1 : request.page();
        int pageSize = request.pageSize() == null || request.pageSize() < 1 ? 20 : request.pageSize();

        List<Long> visibleUserIds = resolveVisibleUserIds();

        Page<CustomerEntity> page = new Page<>(pageNum, pageSize);
        IPage<CustomerEntity> result = customerMapper.pageCustomers(page,
                request.keyword(), request.grade(), request.major(),
                request.progress(), request.intent(), request.ownerUserId(),
                visibleUserIds);

        List<CustomerEntity> entities = result.getRecords();
        if (entities.isEmpty()) {
            return PageResponse.of(List.of(), pageNum, pageSize, result.getTotal());
        }

        Map<Long, String> displayNameMap = buildDisplayNameMap(entities);

        List<CustomerListItemVo> list = entities.stream()
                .map(e -> CustomerConvert.toListItemVo(e,
                        displayNameMap.getOrDefault(e.getOwnerUserId(), ""),
                        displayNameMap.getOrDefault(e.getInviterUserId(), "")))
                .toList();

        return PageResponse.of(list, result.getCurrent(), result.getSize(), result.getTotal());
    }

    @Override
    public CustomerDetailVo getCustomerDetail(Long id) {
        CustomerEntity entity = customerMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "customer not found");
        }

        String ownerDisplayName = lookupDisplayName(entity.getOwnerUserId());
        String inviterDisplayName = lookupDisplayName(entity.getInviterUserId());

        return CustomerConvert.toDetailVo(entity, ownerDisplayName, inviterDisplayName);
    }

    @Override
    public Long createCustomer(CustomerCreateRequest request) {
        CustomerEntity entity = CustomerConvert.toEntity(request);
        customerMapper.insert(entity);
        return entity.getId();
    }

    @Override
    public void updateCustomer(Long id, CustomerUpdateRequest request) {
        CustomerEntity entity = customerMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(ErrorCodeConstants.NOT_FOUND, "customer not found");
        }
        CustomerConvert.updateEntity(entity, request);
        customerMapper.updateById(entity);
    }

    private List<Long> resolveVisibleUserIds() {
        LoginUser currentUser = SecurityUserContext.getCurrentUser();
        if (currentUser == null) {
            return List.of();
        }
        List<Long> ids = new ArrayList<>();
        ids.add(currentUser.userId());
        ids.addAll(DataScopeHelper.getSubordinateIds(currentUser.userId(), userMapper));
        return ids;
    }

    private Map<Long, String> buildDisplayNameMap(List<CustomerEntity> entities) {
        Set<Long> userIds = entities.stream()
                .flatMap(e -> java.util.stream.Stream.of(e.getOwnerUserId(), e.getInviterUserId()))
                .collect(Collectors.toSet());
        if (userIds.isEmpty()) {
            return Map.of();
        }
        return customerMapper.selectUserDisplayNames(List.copyOf(userIds)).stream()
                .collect(Collectors.toMap(
                        CustomerMapper.UserDisplayName::getUserId,
                        CustomerMapper.UserDisplayName::getDisplayName,
                        (a, b) -> a));
    }

    private String lookupDisplayName(Long userId) {
        List<CustomerMapper.UserDisplayName> names = customerMapper.selectUserDisplayNames(List.of(userId));
        return names.isEmpty() ? "" : names.get(0).getDisplayName();
    }
}
