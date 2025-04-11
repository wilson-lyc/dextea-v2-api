package cn.dextea.product.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.CustomizeItemModel;
import cn.dextea.product.model.item.ItemUpdateDTO;
import cn.dextea.product.model.item.ItemCreateRequest;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface ItemService {
    DexteaApiResponse<Void> createItem(Long productId, ItemCreateRequest data);
    DexteaApiResponse<List<CustomizeItemModel>> getItemList(Long productId);
    DexteaApiResponse<CustomizeItemModel> getItemDetail(Long productId);
    DexteaApiResponse<Void> updateItemDetail(Long productId, ItemUpdateDTO data);
    DexteaApiResponse<Void> updateItemStatus(Long id, Integer status);
}
