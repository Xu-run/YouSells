package com.yousells.modules.customer.listener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.yousells.modules.customer.dto.CustomerImportDto;
import com.yousells.modules.customer.entity.CustomerEntity;
import com.yousells.modules.customer.mapper.CustomerMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CustomerImportListener implements ReadListener<CustomerImportDto> {

    private static final int BATCH_COUNT = 50;

    private final CustomerMapper customerMapper;
    private final Long currentUserId;
    private final List<CustomerImportDto> cachedDataList;

    private int successCount = 0;
    private int failCount = 0;

    public CustomerImportListener(CustomerMapper customerMapper, Long currentUserId) {
        this.customerMapper = customerMapper;
        this.currentUserId = currentUserId;
        this.cachedDataList = ListUtils.newArrayListWithExpectedSize(BATCH_COUNT);
    }

    @Override
    public void invoke(CustomerImportDto data, AnalysisContext context) {
        cachedDataList.add(data);
        if (cachedDataList.size() >= BATCH_COUNT) {
            saveData();
            cachedDataList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
        log.info("Customer import finished: success={}, fail={}", successCount, failCount);
    }

    private void saveData() {
        for (CustomerImportDto dto : cachedDataList) {
            try {
                CustomerEntity entity = new CustomerEntity();
                entity.setRealName(dto.getRealName());
                entity.setGrade(dto.getGrade());
                entity.setMajor(dto.getMajor());
                entity.setClassName(dto.getClassName());
                entity.setPhone(dto.getPhone());
                entity.setWechat(dto.getWechat());
                entity.setOwnerUserId(dto.getOwnerUserId() != null ? dto.getOwnerUserId() : currentUserId);
                entity.setInviterUserId(dto.getInviterUserId() != null ? dto.getInviterUserId() : currentUserId);
                entity.setProgress(dto.getProgress() != null ? dto.getProgress() : "职规");
                entity.setIntent(dto.getIntent() != null ? dto.getIntent() : "观望");
                entity.setSourceChannel(dto.getSourceChannel());
                entity.setInviterNote(dto.getInviterNote());
                entity.setCreatedBy(currentUserId);
                entity.setUpdatedBy(currentUserId);
                customerMapper.insert(entity);
                successCount++;
            } catch (Exception e) {
                log.warn("Failed to import customer, row skipped");
                failCount++;
            }
        }
    }

    public int getSuccessCount() {
        return successCount;
    }

    public int getFailCount() {
        return failCount;
    }
}
