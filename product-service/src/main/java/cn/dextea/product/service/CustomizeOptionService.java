package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeOptionCreateDTO;
import cn.dextea.product.dto.CustomizeOptionUpdateDTO;

/**
 * @author Lai Yongchao
 */
public interface CustomizeOptionService {
    ApiResponse create(CustomizeOptionCreateDTO data);
    ApiResponse getList(Long itemId);
    ApiResponse getById(Long id);
    ApiResponse update(Long id, CustomizeOptionUpdateDTO data);
}
