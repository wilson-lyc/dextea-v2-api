package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ProductAdminService {

    ApiResponse<CreateProductResponse> createProduct(CreateProductRequest request);
    ApiResponse<IPage<ProductDetailResponse>> getProductPage(ProductPageQueryRequest request);
    ApiResponse<ProductDetailResponse> getProductDetail(Long id);
    ApiResponse<ProductDetailResponse> updateProduct(Long id, UpdateProductRequest request);
    ApiResponse<Void> deleteProduct(Long id);
}
