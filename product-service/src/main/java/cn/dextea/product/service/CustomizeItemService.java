package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeItemEditDTO;

/**
 * @author Lai Yongchao
 */
public interface CustomizeItemService {
    // 创建
    ApiResponse createItem(Long id, CustomizeItemEditDTO data);
    // 列表
    ApiResponse getItemList(Long id);
    // 单项
    ApiResponse getItemBase(Long productId);
    ApiResponse getItemGlobalStatus(Long productId);
    // 更新
    ApiResponse updateItemBase(Long productId, CustomizeItemEditDTO data);
    ApiResponse updateItemStatus(Long productId, Integer status);
}
