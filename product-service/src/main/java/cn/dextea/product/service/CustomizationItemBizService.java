package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.StorePageQueryCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationItemStatusRequest;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CustomizationItemBizService {

    ApiResponse<IPage<CustomizationItemDetailResponse>> getPage(StorePageQueryCustomizationItemRequest request);

    ApiResponse<Void> updateStatus(Long itemId, UpdateStoreCustomizationItemStatusRequest request);
}
