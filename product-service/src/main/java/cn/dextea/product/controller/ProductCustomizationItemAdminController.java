package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindProductCustomizationItemRequest;
import cn.dextea.product.dto.response.ProductCustomizationItemDetailResponse;
import cn.dextea.product.service.ProductCustomizationItemAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品客制化项目关联管理（Admin）", description = "管理端商品与客制化项目绑定关系接口")
@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductCustomizationItemAdminController {

    private final ProductCustomizationItemAdminService productCustomizationItemAdminService;

    /**
     * 绑定商品客制化项目
     * @param productId 商品ID
     * @param request 绑定客制化项目请求参数
     * @return 操作结果
     */
    @Operation(summary = "绑定商品客制化项目")
    @PostMapping("/{productId}/customization-items")
    public ApiResponse<Void> bindCustomizationItem(
            @Parameter(description = "商品ID") @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId,
            @Valid @RequestBody BindProductCustomizationItemRequest request) {
        return productCustomizationItemAdminService.bindCustomizationItem(productId, request);
    }

    /**
     * 解绑商品客制化项目
     * @param productId 商品ID
     * @param itemId 客制化项目ID
     * @return 操作结果
     */
    @Operation(summary = "解绑商品客制化项目")
    @DeleteMapping("/{productId}/customization-items/{itemId}")
    public ApiResponse<Void> unbindCustomizationItem(
            @Parameter(description = "商品ID") @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId,
            @Parameter(description = "客制化项目ID") @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID不能为空") Long itemId) {
        return productCustomizationItemAdminService.unbindCustomizationItem(productId, itemId);
    }

    /**
     * 获取商品客制化项目列表
     * @param productId 商品ID
     * @return 商品客制化项目详情列表
     */
    @Operation(summary = "获取商品客制化项目列表")
    @GetMapping("/{productId}/customization-items")
    public ApiResponse<List<ProductCustomizationItemDetailResponse>> getProductCustomizationItems(
            @Parameter(description = "商品ID") @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId) {
        return productCustomizationItemAdminService.getProductCustomizationItems(productId);
    }
}
