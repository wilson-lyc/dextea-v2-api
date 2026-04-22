package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.QueryStoreItemOptionsRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionStatusRequest;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;

import java.util.List;

public interface CustomizationOptionBizService {

    ApiResponse<List<CustomizationOptionDetailResponse>> getItemOptionsList(Long itemId,
                                                                            QueryStoreItemOptionsRequest request);

    ApiResponse<Void> updateOptionStoreStatus(Long optionId, UpdateStoreCustomizationOptionStatusRequest request);
}
