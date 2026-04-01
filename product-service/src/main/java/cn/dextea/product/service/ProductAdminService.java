package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ProductAdminService {

    /**
     * 创建商品
     * @param request 商品创建请求（名称、简介、售价、状态）
     * @return 创建成功的商品信息
     */
    ApiResponse<CreateProductResponse> createProduct(CreateProductRequest request);

    /**
     * 分页查询商品列表
     * @param request 分页查询请求（页码、每页条数、名称、状态）
     * @return 商品分页数据
     */
    ApiResponse<IPage<ProductDetailResponse>> getProductPage(ProductPageQueryRequest request);

    /**
     * 查询商品详情
     * @param id 商品ID
     * @return 商品详情
     */
    ApiResponse<ProductDetailResponse> getProductDetail(Long id);

    /**
     * 更新商品信息
     * @param id 商品ID
     * @param request 商品更新请求（名称、简介、售价、状态）
     * @return 更新后的商品详情
     */
    ApiResponse<ProductDetailResponse> updateProduct(Long id, UpdateProductRequest request);

    /**
     * 下架商品（软删除）
     * @param id 商品ID
     * @return 无
     */
    ApiResponse<Void> deleteProduct(Long id);
}
