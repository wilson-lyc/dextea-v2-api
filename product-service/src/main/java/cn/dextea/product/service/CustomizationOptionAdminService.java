package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindOptionIngredientRequest;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.dto.response.OptionIngredientResponse;

public interface CustomizationOptionAdminService {

    ApiResponse<CreateCustomizationOptionResponse> createOption(Long itemId, CreateCustomizationOptionRequest request);

    ApiResponse<CustomizationOptionDetailResponse> updateOption(Long optionId, UpdateCustomizationOptionRequest request);

    ApiResponse<Void> deleteOption(Long optionId);

    ApiResponse<OptionIngredientResponse> bindIngredient(Long optionId, BindOptionIngredientRequest request);

    ApiResponse<Void> unbindIngredient(Long optionId);
}
