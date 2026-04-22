package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductInfoRequest;
import cn.dextea.product.dto.request.UpdateProductGlobalStatusRequest;
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

@Tag(name = "商品管理", description = "适用于公司端管理商品数据")
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
    public ApiResponse<CreateProductResponse> create(
            @Valid @RequestBody CreateProductRequest request) {
        return productAdminService.create(request);
    }

    /**
     * 分页查询商品列表（公司端）
     * @param request 商品名、全局状态、分页参数
     * @return 商品分页列表
     */
    @Operation(summary = "分页查询商品列表（公司端）")
    @GetMapping
    public ApiResponse<IPage<ProductDetailResponse>> getPage(
            @Valid ProductPageQueryRequest request) {
        return productAdminService.getPage(request);
    }

    /**
     * 获取商品详情（公司端）
     * @param id 商品ID
     * @return 商品详情信息
     */
    @Operation(summary = "获取商品详情（公司端）")
    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getDetailById(
            @Parameter(description = "商品ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return productAdminService.getDetailById(id);
    }

    /**
     * 更新商品信息
     * @param id 商品ID
     * @param request 更新商品请求参数
     * @return 更新后的商品详情
     */
    @Operation(summary = "更新商品信息")
    @PutMapping("/{id}/info")
    public ApiResponse<ProductDetailResponse> updateInfo(
            @Parameter(description = "商品ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateProductInfoRequest request) {
        return productAdminService.updateInfo(id, request);
    }

    /**
     * 更新商品全局状态
     * @param id 商品ID
     * @param request 商品新状态
     * @return 操作结果
     */
    @Operation(summary = "更新商品全局状态")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @Parameter(description = "商品ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateProductGlobalStatusRequest request) {
        return productAdminService.updateStatus(id, request);
    }
}
