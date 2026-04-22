package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductInfoRequest;
import cn.dextea.product.dto.request.UpdateProductGlobalStatusRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ProductAdminService {
    ApiResponse<CreateProductResponse> create(CreateProductRequest request);
    ApiResponse<IPage<ProductDetailResponse>> getPage(ProductPageQueryRequest request);
    ApiResponse<ProductDetailResponse> getDetailById(Long id);
    ApiResponse<ProductDetailResponse> updateInfo(Long id, UpdateProductInfoRequest request);
    ApiResponse<Void> updateStatus(Long id, UpdateProductGlobalStatusRequest request);
}
