package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CustomizationOptionListWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionStatusRequest;
import cn.dextea.product.dto.response.CustomizationOptionWithStoreStatusResponse;

import java.util.List;

public interface CustomizationOptionBizService {

    ApiResponse<List<CustomizationOptionWithStoreStatusResponse>> listOptions(Long itemId,
            CustomizationOptionListWithStoreIdRequest request);

    ApiResponse<Void> updateStatus(Long optionId, UpdateStoreCustomizationOptionStatusRequest request);
}
