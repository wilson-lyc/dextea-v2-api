package cn.dextea.store.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.api.dto.response.StoreValidityResponse;
import cn.dextea.store.converter.StoreConverter;
import cn.dextea.store.service.StoreCacheService;
import cn.dextea.store.service.StoreInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StoreInternalServiceImpl implements StoreInternalService {
    private final StoreCacheService storeCacheService;
    private final StoreConverter storeConverter;

    @Override
    public ApiResponse<StoreValidityResponse> checkValidity(Long id) {
        boolean valid = storeCacheService.checkValidity(id);
        return ApiResponse.success(storeConverter.toStoreValidityResponse(id, valid));
    }
}
