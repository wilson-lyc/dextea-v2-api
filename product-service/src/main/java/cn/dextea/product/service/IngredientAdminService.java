package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateIngredientRequest;
import cn.dextea.product.dto.request.IngredientPageQueryRequest;
import cn.dextea.product.dto.request.UpdateIngredientRequest;
import cn.dextea.product.dto.response.CreateIngredientResponse;
import cn.dextea.product.dto.response.IngredientDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface IngredientAdminService {

    ApiResponse<CreateIngredientResponse> createIngredient(CreateIngredientRequest request);

    ApiResponse<IPage<IngredientDetailResponse>> getIngredientPage(IngredientPageQueryRequest request);

    ApiResponse<IngredientDetailResponse> getIngredientDetail(Long id);

    ApiResponse<IngredientDetailResponse> updateIngredient(Long id, UpdateIngredientRequest request);

    ApiResponse<Void> deleteIngredient(Long id);
}
