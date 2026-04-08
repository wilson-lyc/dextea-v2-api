package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.IngredientPageQueryRequest;
import cn.dextea.product.dto.request.UpdateStoreIngredientInventoryRequest;
import cn.dextea.product.dto.response.StoreIngredientInventoryResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface IngredientBizService {

    ApiResponse<IPage<StoreIngredientInventoryResponse>> getInventoryPage(Long storeId, IngredientPageQueryRequest request);

    ApiResponse<Void> updateInventory(Long ingredientId, UpdateStoreIngredientInventoryRequest request);
}
