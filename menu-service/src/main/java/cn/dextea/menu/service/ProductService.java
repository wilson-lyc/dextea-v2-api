package cn.dextea.menu.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuProductModel;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface ProductService {
    DexteaApiResponse<Void> addProduct(Long menuId, String groupId, Long productId, Integer sort);
    DexteaApiResponse<Void> deleteProduct(Long menuId, String groupId, Long productId);
    DexteaApiResponse<List<MenuProductModel>> getProductList(Long menuId, String groupId);
    DexteaApiResponse<MenuProductModel> getProductInfo(Long menuId, String groupId, Long productId);
    DexteaApiResponse<Void> updateProductInfo(Long menuId, String groupId, Long productId, Integer sort);
}
