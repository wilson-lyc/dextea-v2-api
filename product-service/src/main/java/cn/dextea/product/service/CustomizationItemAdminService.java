package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.CustomizationItemPageQueryRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemStatusRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CustomizationItemAdminService {

    ApiResponse<CreateCustomizationItemResponse> create(CreateCustomizationItemRequest request);

    ApiResponse<IPage<CustomizationItemDetailResponse>> getPage(CustomizationItemPageQueryRequest request);

    ApiResponse<CustomizationItemDetailResponse> getDetail(Long id);

    ApiResponse<CustomizationItemDetailResponse> updateInfo(Long id, UpdateCustomizationItemRequest request);

    ApiResponse<Void> updateStatus(Long id, UpdateCustomizationItemStatusRequest request);
}
