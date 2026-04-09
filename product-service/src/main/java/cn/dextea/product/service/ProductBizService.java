package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.ProductPageQueryWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreProductSaleRequest;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ProductBizService {

    ApiResponse<IPage<ProductDetailResponse>> getProductPage(ProductPageQueryWithStoreIdRequest request);
    ApiResponse<Void> updateSaleStatus(Long productId, UpdateStoreProductSaleRequest request);
    ApiResponse<ProductBizDetailResponse> getProductDetail(Long productId, Long storeId);
}
