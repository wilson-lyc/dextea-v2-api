package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.StoreProductPageRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ProductBizService {

    ApiResponse<IPage<ProductDetailResponse>> getProductPage(StoreProductPageRequest request);
    ApiResponse<Void> updateStatus(Long productId, UpdateStoreProductStatusRequest request);
    ApiResponse<ProductBizDetailResponse> getProductDetail(Long productId, Long storeId);
}
