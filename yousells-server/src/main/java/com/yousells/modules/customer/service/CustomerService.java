package com.yousells.modules.customer.service;

import com.yousells.common.response.PageResponse;
import com.yousells.modules.customer.dto.CustomerCreateRequest;
import com.yousells.modules.customer.dto.CustomerNextFollowRequest;
import com.yousells.modules.customer.dto.CustomerQueryRequest;
import com.yousells.modules.customer.dto.CustomerTagsUpdateRequest;
import com.yousells.modules.customer.dto.CustomerUpdateRequest;
import com.yousells.modules.customer.vo.CustomerDetailVo;
import com.yousells.modules.customer.vo.CustomerListItemVo;
import com.yousells.modules.customer.vo.CustomerTagVo;

import java.util.List;

public interface CustomerService {

    PageResponse<CustomerListItemVo> pageCustomers(CustomerQueryRequest request);

    CustomerDetailVo getCustomerDetail(Long id);

    Long createCustomer(CustomerCreateRequest request);

    void updateCustomer(Long id, CustomerUpdateRequest request);

    List<CustomerTagVo> listTags();

    void updateTags(Long id, CustomerTagsUpdateRequest request);

    void updateNextFollow(Long id, CustomerNextFollowRequest request);
}
