package cn.dextea.store.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.dto.request.CreateStoreRequest;
import cn.dextea.store.dto.request.StorePageQueryRequest;
import cn.dextea.store.dto.request.UpdateStoreRequest;
import cn.dextea.store.dto.response.CreateStoreResponse;
import cn.dextea.store.dto.response.StoreDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface StoreAdminService {
    ApiResponse<CreateStoreResponse> create(CreateStoreRequest request);

    ApiResponse<IPage<StoreDetailResponse>> page(StorePageQueryRequest request);

    ApiResponse<StoreDetailResponse> detail(Long id);

    ApiResponse<StoreDetailResponse> update(Long id, UpdateStoreRequest request);

    ApiResponse<Void> delete(Long id);
}
