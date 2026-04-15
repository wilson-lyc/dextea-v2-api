package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateOptionRequest;
import cn.dextea.product.dto.request.UpdateOptionStatusRequest;
import cn.dextea.product.dto.request.UpdateOptionInfoRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.OptionDetailResponse;

import java.util.List;

public interface CustomizationOptionAdminService {

    ApiResponse<CreateCustomizationOptionResponse> createOption(Long itemId, CreateOptionRequest request);

    ApiResponse<List<OptionDetailResponse>> getItemOptionsList(Long itemId);

    ApiResponse<OptionDetailResponse> updateOptionInfo(Long id, UpdateOptionInfoRequest request);

    ApiResponse<Void> updateOptionStatus(Long id, UpdateOptionStatusRequest request);

    ApiResponse<OptionDetailResponse> getOptionDetail(Long id);
}
