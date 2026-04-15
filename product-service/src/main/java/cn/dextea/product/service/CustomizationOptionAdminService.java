package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionStatusRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionInfoRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;

import java.util.List;

public interface CustomizationOptionAdminService {

    ApiResponse<CreateCustomizationOptionResponse> createOption(Long itemId, CreateCustomizationOptionRequest request);

    ApiResponse<List<CustomizationOptionDetailResponse>> getItemOptionsList(Long itemId);

    ApiResponse<CustomizationOptionDetailResponse> updateOptionInfo(Long id, UpdateCustomizationOptionInfoRequest request);

    ApiResponse<Void> updateOptionStatus(Long id, UpdateCustomizationOptionStatusRequest request);

    ApiResponse<CustomizationOptionDetailResponse> getOptionDetail(Long id);
}
