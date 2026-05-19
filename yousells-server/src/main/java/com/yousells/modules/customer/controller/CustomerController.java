package com.yousells.modules.customer.controller;

import com.yousells.common.response.ApiResponse;
import com.yousells.common.response.IdResponse;
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
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ApiResponse<PageResponse<CustomerListItemVo>> page(CustomerQueryRequest request) {
        return ApiResponse.success(customerService.pageCustomers(request));
    }

    @GetMapping("/tags")
    public ApiResponse<List<CustomerTagVo>> listTags() {
        return ApiResponse.success(customerService.listTags());
    }

    @GetMapping("/{id}")
    public ApiResponse<CustomerDetailVo> detail(@PathVariable Long id) {
        return ApiResponse.success(customerService.getCustomerDetail(id));
    }

    @PostMapping
    public ApiResponse<IdResponse> create(@Valid @RequestBody CustomerCreateRequest request) {
        return ApiResponse.success(new IdResponse(customerService.createCustomer(request)));
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody CustomerUpdateRequest request) {
        customerService.updateCustomer(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/tags")
    public ApiResponse<Void> updateTags(@PathVariable Long id, @Valid @RequestBody CustomerTagsUpdateRequest request) {
        customerService.updateTags(id, request);
        return ApiResponse.success();
    }

    @PutMapping("/{id}/next-follow")
    public ApiResponse<Void> updateNextFollow(@PathVariable Long id, @Valid @RequestBody CustomerNextFollowRequest request) {
        customerService.updateNextFollow(id, request);
        return ApiResponse.success();
    }
}
