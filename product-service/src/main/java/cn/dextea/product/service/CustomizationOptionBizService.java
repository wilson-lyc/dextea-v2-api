package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.ItemOptionsListInStore;
import cn.dextea.product.dto.request.UpdateOptionStoreStatusRequest;
import cn.dextea.product.dto.response.OptionDetailResponse;

import java.util.List;

public interface CustomizationOptionBizService {

    ApiResponse<List<OptionDetailResponse>> getItemOptionsList(Long itemId,
                                                               ItemOptionsListInStore request);

    ApiResponse<Void> updateOptionStoreStatus(Long optionId, UpdateOptionStoreStatusRequest request);
}
