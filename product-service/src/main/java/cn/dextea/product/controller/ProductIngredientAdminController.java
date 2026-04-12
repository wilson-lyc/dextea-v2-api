package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindProductIngredientRequest;
import cn.dextea.product.dto.response.ProductIngredientDetailResponse;
import cn.dextea.product.service.ProductIngredientAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品原料关联管理（Admin）", description = "管理端商品与原料绑定关系接口")
@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductIngredientAdminController {

    private final ProductIngredientAdminService productIngredientAdminService;

    /**
     * 绑定商品原料
     * @param productId 商品ID
     * @param request 绑定原料请求参数
     * @return 操作结果
     */
    @Operation(summary = "绑定商品原料")
    @PostMapping("/{productId}/ingredients")
    public ApiResponse<Void> bindIngredient(
            @Parameter(description = "商品ID") @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId,
            @Valid @RequestBody BindProductIngredientRequest request) {
        return productIngredientAdminService.bindIngredient(productId, request);
    }

    /**
     * 解绑商品原料
     * @param productId 商品ID
     * @param ingredientId 原料ID
     * @return 操作结果
     */
    @Operation(summary = "解绑商品原料")
    @DeleteMapping("/{productId}/ingredients/{ingredientId}")
    public ApiResponse<Void> unbindIngredient(
            @Parameter(description = "商品ID") @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId,
            @Parameter(description = "原料ID") @PathVariable("ingredientId") @Min(value = 1, message = "原料ID不能为空") Long ingredientId) {
        return productIngredientAdminService.unbindIngredient(productId, ingredientId);
    }

    /**
     * 获取商品原料列表
     * @param productId 商品ID
     * @return 商品原料详情列表
     */
    @Operation(summary = "获取商品原料列表")
    @GetMapping("/{productId}/ingredients")
    public ApiResponse<List<ProductIngredientDetailResponse>> getProductIngredients(
            @Parameter(description = "商品ID") @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId) {
        return productIngredientAdminService.getProductIngredients(productId);
    }
}