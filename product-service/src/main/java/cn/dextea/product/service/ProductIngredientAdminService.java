package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindProductIngredientRequest;
import cn.dextea.product.dto.response.ProductIngredientDetailResponse;

import java.util.List;

public interface ProductIngredientAdminService {

    ApiResponse<Void> bindIngredient(Long productId, BindProductIngredientRequest request);

    ApiResponse<Void> unbindIngredient(Long productId, Long ingredientId);

    ApiResponse<List<ProductIngredientDetailResponse>> getProductIngredients(Long productId);
}
