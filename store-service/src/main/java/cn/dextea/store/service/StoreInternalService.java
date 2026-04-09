package cn.dextea.store.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.api.dto.response.StoreValidityResponse;

public interface StoreInternalService {
    ApiResponse<StoreValidityResponse> checkValidity(Long id);
}
