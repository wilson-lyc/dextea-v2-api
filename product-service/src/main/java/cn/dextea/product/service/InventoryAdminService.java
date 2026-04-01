package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.AdjustInventoryRequest;
import cn.dextea.product.dto.request.InventoryPageQueryRequest;
import cn.dextea.product.dto.request.SetInventoryRequest;
import cn.dextea.product.dto.response.InventoryDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface InventoryAdminService {

    /**
     * Upsert the inventory record for a specific ingredient in a specific store.
     * Creates a new record if none exists, otherwise updates quantity / unit / warnThreshold.
     */
    ApiResponse<InventoryDetailResponse> setInventory(Long storeId, Long ingredientId, SetInventoryRequest request);

    /**
     * Adjust the stock quantity by a signed delta (positive = restock, negative = consumption).
     * Returns INSUFFICIENT_STOCK when the resulting quantity would be negative.
     */
    ApiResponse<InventoryDetailResponse> adjustInventory(Long storeId, Long ingredientId, AdjustInventoryRequest request);

    /** Paginated list of inventory records for a given store. */
    ApiResponse<IPage<InventoryDetailResponse>> getInventoryPage(Long storeId, InventoryPageQueryRequest request);

    /** Single inventory detail for a store + ingredient pair. */
    ApiResponse<InventoryDetailResponse> getInventoryDetail(Long storeId, Long ingredientId);
}
