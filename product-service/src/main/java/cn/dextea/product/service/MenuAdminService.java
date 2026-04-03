package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateMenuRequest;
import cn.dextea.product.dto.request.MenuPageQueryRequest;
import cn.dextea.product.dto.request.UpdateMenuRequest;
import cn.dextea.product.dto.response.CreateMenuResponse;
import cn.dextea.product.dto.response.MenuDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface MenuAdminService {

    ApiResponse<CreateMenuResponse> createMenu(CreateMenuRequest request);

    ApiResponse<IPage<MenuDetailResponse>> getMenuPage(MenuPageQueryRequest request);

    ApiResponse<MenuDetailResponse> getMenuDetail(Long id);

    ApiResponse<MenuDetailResponse> updateMenu(Long id, UpdateMenuRequest request);

    ApiResponse<Void> deleteMenu(Long id);
}
