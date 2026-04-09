package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.CustomizationItemPageQueryRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CustomizationItemAdminService {

    ApiResponse<CreateCustomizationItemResponse> create(CreateCustomizationItemRequest request);

    ApiResponse<IPage<CustomizationItemDetailResponse>> page(CustomizationItemPageQueryRequest request);

    ApiResponse<CustomizationItemDetailResponse> detail(Long id);

    ApiResponse<CustomizationItemDetailResponse> update(Long id, UpdateCustomizationItemRequest request);

    ApiResponse<Void> delete(Long id);
}
