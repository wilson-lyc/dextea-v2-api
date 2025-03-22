package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.item.ItemUpdateDTO;
import cn.dextea.product.dto.item.ItemCreateDTO;

/**
 * @author Lai Yongchao
 */
public interface ItemService {
    // 创建
    ApiResponse createItem(Long productId, ItemCreateDTO data);
    // 列表
    ApiResponse getItemList(Long productId);
    // 单项
    ApiResponse getItemInfo(Long productId);
    // 更新
    ApiResponse updateItemInfo(Long productId, ItemUpdateDTO data);
}
