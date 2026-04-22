package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.QueryStoreMenuRequest;
import cn.dextea.product.dto.response.StoreMenuResponse;

public interface MenuBizService {

    ApiResponse<StoreMenuResponse> getStoreMenu(QueryStoreMenuRequest request);
}
