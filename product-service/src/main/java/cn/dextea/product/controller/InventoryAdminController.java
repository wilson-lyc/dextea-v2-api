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
     * List all inventory records for a store (paginated).
     * Supports optional filters: ingredientName (fuzzy), lowStock (boolean).
     */
    @GetMapping
    public ApiResponse<IPage<InventoryDetailResponse>> getInventoryPage(
            @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId,
            @Valid InventoryPageQueryRequest request) {
        return inventoryAdminService.getInventoryPage(storeId, request);
    }

    /**
     * Get inventory detail for a specific ingredient in a store.
     */
    @GetMapping("/{ingredientId}")
    public ApiResponse<InventoryDetailResponse> getInventoryDetail(
            @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId,
            @PathVariable("ingredientId") @Min(value = 1, message = "原料ID不能为空") Long ingredientId) {
        return inventoryAdminService.getInventoryDetail(storeId, ingredientId);
    }

    /**
     * Set (upsert) the inventory for a specific ingredient in a store.
     * Creates a new record if none exists, otherwise updates it.
     */
    @PutMapping("/{ingredientId}")
    public ApiResponse<InventoryDetailResponse> setInventory(
            @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId,
            @PathVariable("ingredientId") @Min(value = 1, message = "原料ID不能为空") Long ingredientId,
            @Valid @RequestBody SetInventoryRequest request) {
        return inventoryAdminService.setInventory(storeId, ingredientId, request);
    }

    /**
     * Adjust the stock quantity by a signed delta.
     * Positive delta = restock; negative delta = consumption.
     */
    @PostMapping("/{ingredientId}/adjust")
    public ApiResponse<InventoryDetailResponse> adjustInventory(
            @PathVariable("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId,
            @PathVariable("ingredientId") @Min(value = 1, message = "原料ID不能为空") Long ingredientId,
            @Valid @RequestBody AdjustInventoryRequest request) {
        return inventoryAdminService.adjustInventory(storeId, ingredientId, request);
    }
}
