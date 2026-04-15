package cn.dextea.product.service.impl;

import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.enums.CustomizationStatus;

class CustomizationTestFixtures {

    static CustomizationItemEntity buildActiveItem(Long id, String name) {
        return CustomizationItemEntity.builder()
                .id(id)
                .name(name)
                .status(CustomizationStatus.ACTIVE.getValue())
                .build();
    }
}
