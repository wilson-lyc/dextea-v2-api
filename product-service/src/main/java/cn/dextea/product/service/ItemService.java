package cn.dextea.product.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.CustomizeItemModel;
import cn.dextea.product.model.item.ItemUpdateRequest;
import cn.dextea.product.model.item.ItemCreateRequest;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface ItemService {
    DexteaApiResponse<Void> createItem(Long productId, ItemCreateRequest data);
    DexteaApiResponse<List<CustomizeItemModel>> getItemList(Long productId);
    DexteaApiResponse<CustomizeItemModel> getItemDetail(Long productId);
    DexteaApiResponse<Void> updateItemDetail(Long productId, ItemUpdateRequest data);
    DexteaApiResponse<Void> updateItemStatus(Long id, Integer status);
}
