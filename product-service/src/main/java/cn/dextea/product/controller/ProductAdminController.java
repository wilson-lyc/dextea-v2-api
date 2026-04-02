package cn.dextea.product.controller;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.service.ProductAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
@Validated
public class ProductAdminController {

    private final ProductAdminService productAdminService;

    /**
     * 创建商品
     * @param request 创建商品请求参数
     * @return 创建成功的商品信息
     */
    @PostMapping
    public ApiResponse<CreateProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return productAdminService.createProduct(request);
    }

    /**
     * 分页查询商品列表
     * @param request 查询请求参数（支持按名称模糊搜索、按状态筛选）
     * @return 商品分页列表
     */
    @GetMapping
    public ApiResponse<IPage<ProductDetailResponse>> getProductPage(
            @Valid ProductPageQueryRequest request) {
        return productAdminService.getProductPage(request);
    }

    /**
     * 获取商品详情
     * @param id 商品ID
     * @return 商品详情信息
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProductDetail(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return productAdminService.getProductDetail(id);
    }

    /**
     * 更新商品信息
     * @param id 商品ID
     * @param request 更新商品请求参数
     * @return 更新后的商品详情
     */
    @PutMapping("/{id}")
    public ApiResponse<ProductDetailResponse> updateProduct(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productAdminService.updateProduct(id, request);
    }

    /**
     * 删除商品（下架）
     * @param id 商品ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return productAdminService.deleteProduct(id);
    }
}