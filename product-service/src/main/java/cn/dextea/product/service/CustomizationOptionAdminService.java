package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;

import java.util.List;

public interface CustomizationOptionAdminService {

    ApiResponse<CreateCustomizationOptionResponse> createOption(Long itemId, CreateCustomizationOptionRequest request);

    ApiResponse<List<CustomizationOptionDetailResponse>> listOptions(Long itemId);

    ApiResponse<CustomizationOptionDetailResponse> updateOption(Long id, UpdateCustomizationOptionRequest request);

    ApiResponse<Void> deleteOption(Long id);
}
