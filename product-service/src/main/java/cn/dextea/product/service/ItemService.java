package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.item.ItemUpdateDTO;
import cn.dextea.product.dto.item.ItemCreateDTO;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface ItemService {
    ApiResponse createItem(Long productId, ItemCreateDTO data);
    ApiResponse getItemList(Long productId);
    ApiResponse getItemInfo(Long productId) throws NotFoundException;
    ApiResponse updateItemInfo(Long productId, ItemUpdateDTO data) throws NotFoundException;
}
