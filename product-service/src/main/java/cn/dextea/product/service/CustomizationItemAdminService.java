package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;

import java.util.List;

public interface CustomizationItemAdminService {

    ApiResponse<CreateCustomizationItemResponse> createItem(Long productId, CreateCustomizationItemRequest request);

    ApiResponse<List<CustomizationItemDetailResponse>> getProductCustomizations(Long productId);

    ApiResponse<CustomizationItemDetailResponse> updateItem(Long itemId, UpdateCustomizationItemRequest request);

    ApiResponse<Void> deleteItem(Long itemId);
}
