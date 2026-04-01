package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BatchSetStoreProductStatusRequest;
import cn.dextea.product.dto.request.SetStoreProductStatusRequest;
import cn.dextea.product.dto.response.StoreProductStatusDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface StoreProductStatusAdminService {

    ApiResponse<StoreProductStatusDetailResponse> setStatus(SetStoreProductStatusRequest request);

    ApiResponse<StoreProductStatusDetailResponse> getStatus(Long storeId, Long productId);

    ApiResponse<IPage<StoreProductStatusDetailResponse>> getStatusPageByStore(Long storeId, Long productId, Long current, Long size);

    ApiResponse<Void> batchSetStatus(BatchSetStoreProductStatusRequest request);
}