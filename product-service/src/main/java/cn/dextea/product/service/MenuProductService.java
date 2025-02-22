package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuProductCreateDTO;
import cn.dextea.product.dto.MenuProductUpdateDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface MenuProductService {
    ApiResponse menuBindProduct(MenuProductCreateDTO data);
    ApiResponse menuUnbindProduct(Long groupId, Long productId);
    ApiResponse getProductsByGroupId(Long id);
    ApiResponse getMenuBindProductInfo(Long groupId, Long productId);
    ApiResponse updateMenuBindProductInfo(Long groupId, Long productId, MenuProductUpdateDTO data);
}
