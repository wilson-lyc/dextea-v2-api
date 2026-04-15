package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CustomizationOptionListWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionStatusRequest;
import cn.dextea.product.dto.response.OptionDetailResponse;

import java.util.List;

public interface CustomizationOptionBizService {

    ApiResponse<List<OptionDetailResponse>> getItemOptionsList(Long itemId,
                                                               CustomizationOptionListWithStoreIdRequest request);

    ApiResponse<Void> updateStatus(Long optionId, UpdateStoreCustomizationOptionStatusRequest request);
}
