package cn.dextea.store.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.api.dto.response.StoreValidityResponse;
import cn.dextea.store.converter.StoreConverter;
import cn.dextea.store.entity.StoreEntity;
import cn.dextea.store.enums.StoreStatus;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.service.StoreInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreInternalServiceImpl implements StoreInternalService {
    private final StoreMapper storeMapper;
    private final StoreConverter storeConverter;

    @Override
    public ApiResponse<StoreValidityResponse> checkValidity(Long id) {
        StoreEntity storeEntity = storeMapper.selectById(id);
        boolean valid = storeEntity != null && storeEntity.getStatus() != StoreStatus.CLOSED.getValue();
        return ApiResponse.success(storeConverter.toStoreValidityResponse(id, valid));
    }
}
