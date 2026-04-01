package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindProductIngredientRequest;
import cn.dextea.product.dto.response.ProductIngredientDetailResponse;
import cn.dextea.product.service.ProductIngredientAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @PostMapping("/{productId}/ingredients")
    public ApiResponse<Void> bindIngredient(
            @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId,
            @Valid @RequestBody BindProductIngredientRequest request) {
        return productIngredientAdminService.bindIngredient(productId, request);
    }

    /**
     * 解绑商品原料
     * @param productId 商品ID
     * @param ingredientId 原料ID
     * @return 操作结果
     */
    @DeleteMapping("/{productId}/ingredients/{ingredientId}")
    public ApiResponse<Void> unbindIngredient(
            @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId,
            @PathVariable("ingredientId") @Min(value = 1, message = "原料ID不能为空") Long ingredientId) {
        return productIngredientAdminService.unbindIngredient(productId, ingredientId);
    }

    /**
     * 获取商品原料列表
     * @param productId 商品ID
     * @return 商品原料详情列表
     */
    @GetMapping("/{productId}/ingredients")
    public ApiResponse<List<ProductIngredientDetailResponse>> getProductIngredients(
            @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId) {
        return productIngredientAdminService.getProductIngredients(productId);
    }
}