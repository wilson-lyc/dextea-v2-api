package cn.dextea.store.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.dto.request.CreateStoreRequest;
import cn.dextea.store.dto.request.StorePageQueryRequest;
import cn.dextea.store.dto.request.UpdateStoreRequest;
import cn.dextea.store.dto.response.StoreDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface StoreAdminService {
    ApiResponse<StoreDetailResponse> createStore(CreateStoreRequest request);

    ApiResponse<IPage<StoreDetailResponse>> getStorePage(StorePageQueryRequest request);

    ApiResponse<StoreDetailResponse> getStoreDetail(Long id);

    ApiResponse<StoreDetailResponse> updateStore(Long id, UpdateStoreRequest request);

    ApiResponse<Void> deleteStore(Long id);
}
