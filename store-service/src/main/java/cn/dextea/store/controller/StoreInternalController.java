package cn.dextea.store.controller;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.api.dto.response.StoreValidityResponse;
import cn.dextea.store.entity.StoreEntity;
import cn.dextea.store.enums.StoreStatus;
import cn.dextea.store.mapper.StoreMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/internal/stores")
@RequiredArgsConstructor
public class StoreInternalController {
    private final StoreMapper storeMapper;

    @GetMapping("/{id}/validity")
    public ApiResponse<StoreValidityResponse> checkStoreValidity(@PathVariable("id") Long id) {
        StoreEntity storeEntity = storeMapper.selectById(id);
        boolean valid = storeEntity != null && storeEntity.getStatus() != StoreStatus.CLOSED.getValue();
        return ApiResponse.success(StoreValidityResponse.builder()
                .storeId(id)
                .valid(valid)
                .build());
    }
}
