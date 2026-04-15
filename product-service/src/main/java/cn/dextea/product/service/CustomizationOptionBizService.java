package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.ItemOptionsListInStore;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionStatusRequest;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;

import java.util.List;

public interface CustomizationOptionBizService {

    ApiResponse<List<CustomizationOptionDetailResponse>> getItemOptionsList(Long itemId,
                                                                            ItemOptionsListInStore request);

    ApiResponse<Void> updateOptionStoreStatus(Long optionId, UpdateStoreCustomizationOptionStatusRequest request);
}
