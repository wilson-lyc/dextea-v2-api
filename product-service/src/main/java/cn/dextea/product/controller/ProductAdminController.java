package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.service.ProductAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品管理接口", description = "适用于公司端管理商品数据")
@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductAdminController {

    private final ProductAdminService productAdminService;

    /**
     * 创建商品
     * @param request 创建商品请求参数
     * @return 创建成功的商品信息
     */
    @Operation(summary = "创建商品")
    @PostMapping
    public ApiResponse<CreateProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return productAdminService.createProduct(request);
    }

    /**
     * 分页查询商品列表
     * @param request 商品名、全局状态、分页参数
     * @return 商品分页列表
     */
    @Operation(summary = "分页查询商品列表")
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
    @Operation(summary = "获取商品详情")
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProductDetail(
            @Parameter(description = "商品ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return productAdminService.getProductDetail(id);
    }

    /**
     * 更新商品信息
     * @param id 商品ID
     * @param request 更新商品请求参数
     * @return 更新后的商品详情
     */
    @Operation(summary = "更新商品信息")
    @PutMapping("/{id}")
    public ApiResponse<ProductDetailResponse> updateProduct(
            @Parameter(description = "商品ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productAdminService.updateProduct(id, request);
    }

    /**
     * 下架商品
     * @param id 商品ID
     * @return 操作结果
     */
    @Operation(summary = "下架商品")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(
            @Parameter(description = "商品ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return productAdminService.deleteProduct(id);
    }
}
