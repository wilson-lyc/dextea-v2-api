package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.AdjustInventoryRequest;
import cn.dextea.product.dto.request.InventoryPageQueryRequest;
import cn.dextea.product.dto.request.SetInventoryRequest;
import cn.dextea.product.dto.response.InventoryDetailResponse;
import cn.dextea.product.service.InventoryAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/ingredient-inventories/stores/{storeId}")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class InventoryAdminController {

    private final InventoryAdminService inventoryAdminService;

    /**
     * 分页查询门店原料库存列表
     * @param storeId 门店ID
     * @param request 查询请求参数（支持按原料名称模糊搜索、按低库存筛选）
     * @return 门店原料库存分页列表
     */
    @GetMapping
    public ApiResponse<IPage<InventoryDetailResponse>> getInventoryPage(
            @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId,
            @Valid InventoryPageQueryRequest request) {
        return inventoryAdminService.getInventoryPage(storeId, request);
    }

    /**
     * 获取门店原料库存详情
     * @param storeId 门店ID
     * @param ingredientId 原料ID
     * @return 门店原料库存详情
     */
    @GetMapping("/{ingredientId}")
    public ApiResponse<InventoryDetailResponse> getInventoryDetail(
            @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId,
            @PathVariable("ingredientId") @Min(value = 1, message = "原料ID不能为空") Long ingredientId) {
        return inventoryAdminService.getInventoryDetail(storeId, ingredientId);
    }

    /**
     * 设置门店原料库存（Upsert）
     * @param storeId 门店ID
     * @param ingredientId 原料ID
     * @param request 设置库存请求参数
     * @return 设置后的库存详情
     */
    @PutMapping("/{ingredientId}")
    public ApiResponse<InventoryDetailResponse> setInventory(
            @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId,
            @PathVariable("ingredientId") @Min(value = 1, message = "原料ID不能为空") Long ingredientId,
            @Valid @RequestBody SetInventoryRequest request) {
        return inventoryAdminService.setInventory(storeId, ingredientId, request);
    }

    /**
     * 调整门店原料库存数量
     * @param storeId 门店ID
     * @param ingredientId 原料ID
     * @param request 调整库存请求参数（正数=补货，负数=消耗）
     * @return 调整后的库存详情
     */
    @PostMapping("/{ingredientId}/adjust")
    public ApiResponse<InventoryDetailResponse> adjustInventory(
            @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId,
            @PathVariable("ingredientId") @Min(value = 1, message = "原料ID不能为空") Long ingredientId,
            @Valid @RequestBody AdjustInventoryRequest request) {
        return inventoryAdminService.adjustInventory(storeId, ingredientId, request);
    }
}