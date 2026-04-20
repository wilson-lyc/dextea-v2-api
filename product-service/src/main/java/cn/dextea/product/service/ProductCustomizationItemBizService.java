package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.response.StoreProductCustomizationItemDetailResponse;

import java.util.List;

public interface ProductCustomizationItemBizService {

    ApiResponse<List<StoreProductCustomizationItemDetailResponse>> getProductCustomizationItems(Long productId, Long storeId);
}
