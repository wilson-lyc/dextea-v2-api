package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindProductCustomizationItemRequest;
import cn.dextea.product.dto.response.ProductCustomizationItemDetailResponse;

import java.util.List;

public interface ProductCustomizationItemAdminService {

    ApiResponse<Void> bindCustomizationItem(Long productId, BindProductCustomizationItemRequest request);

    ApiResponse<Void> unbindCustomizationItem(Long productId, Long itemId);

    ApiResponse<List<ProductCustomizationItemDetailResponse>> getProductCustomizationItems(Long productId);
}
