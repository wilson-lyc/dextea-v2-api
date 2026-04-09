package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CustomizationItemPageQueryWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationItemSaleRequest;
import cn.dextea.product.dto.response.CustomizationItemWithStoreStatusResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface CustomizationItemBizService {

    ApiResponse<IPage<CustomizationItemWithStoreStatusResponse>> page(CustomizationItemPageQueryWithStoreIdRequest request);

    ApiResponse<Void> updateSaleStatus(Long itemId, UpdateStoreCustomizationItemSaleRequest request);
}
