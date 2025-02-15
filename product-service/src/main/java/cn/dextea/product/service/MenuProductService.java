package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuProductCreateDTO;
import cn.dextea.product.dto.MenuProductUpdateDTO;
import jakarta.validation.Valid;

/**
 * @author Lai Yongchao
 */
public interface MenuProductService {
    ApiResponse createMenuProduct(MenuProductCreateDTO data);
    ApiResponse getMenuProductListByTypeId(Long typeId);
    ApiResponse unbindProduct(Long typeId, Long productId);
    ApiResponse getMenuProductBase(Long typeId, Long productId);
    ApiResponse updateMenuProductBase(Long typeId, Long productId, @Valid MenuProductUpdateDTO data);
}
