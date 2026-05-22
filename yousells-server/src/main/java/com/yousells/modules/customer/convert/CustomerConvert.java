package com.yousells.modules.customer.convert;

import com.yousells.modules.customer.dto.CustomerCreateRequest;
import com.yousells.modules.customer.dto.CustomerUpdateRequest;
import com.yousells.modules.customer.entity.CustomerEntity;
import com.yousells.modules.customer.vo.CustomerDetailVo;
import com.yousells.modules.customer.vo.CustomerListItemVo;

public class CustomerConvert {

    private CustomerConvert() {
    }

    public static CustomerListItemVo toListItemVo(CustomerEntity entity, String ownerDisplayName,
                                                   String inviterDisplayName) {
        return new CustomerListItemVo(
                entity.getId(),
                entity.getRealName(),
                entity.getGrade(),
                entity.getMajor(),
                entity.getClassName(),
                entity.getProgress(),
                entity.getIntent(),
                ownerDisplayName,
                inviterDisplayName,
                entity.getCreatedAt()
        );
    }

    public static CustomerDetailVo toDetailVo(CustomerEntity entity, String ownerDisplayName,
                                               String inviterDisplayName) {
        return new CustomerDetailVo(
                entity.getId(),
                entity.getRealName(),
                entity.getGrade(),
                entity.getMajor(),
                entity.getClassName(),
                entity.getInviterUserId(),
                inviterDisplayName,
                entity.getOwnerUserId(),
                ownerDisplayName,
                entity.getProgress(),
                entity.getIntent(),
                entity.getInviterNote(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    public static CustomerEntity toEntity(CustomerCreateRequest request) {
        CustomerEntity entity = new CustomerEntity();
        entity.setRealName(request.realName());
        entity.setGrade(request.grade());
        entity.setMajor(request.major());
        entity.setClassName(request.className());
        entity.setInviterUserId(request.inviterUserId());
        entity.setOwnerUserId(request.ownerUserId());
        entity.setProgress(request.progress() != null ? request.progress() : "职规");
        entity.setIntent(request.intent() != null ? request.intent() : "观望");
        entity.setInviterNote(request.inviterNote());
        return entity;
    }

    public static void updateEntity(CustomerEntity entity, CustomerUpdateRequest request) {
        entity.setRealName(request.realName());
        entity.setGrade(request.grade());
        entity.setMajor(request.major());
        entity.setClassName(request.className());
        entity.setInviterUserId(request.inviterUserId());
        entity.setOwnerUserId(request.ownerUserId());
        entity.setProgress(request.progress());
        entity.setIntent(request.intent());
        entity.setInviterNote(request.inviterNote());
    }
}
