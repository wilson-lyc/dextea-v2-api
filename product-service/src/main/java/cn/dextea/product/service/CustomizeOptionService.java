package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.option.CustomizeOptionCreateDTO;
import cn.dextea.product.dto.option.CustomizeOptionUpdateDTO;

/**
 * @author Lai Yongchao
 */
public interface CustomizeOptionService {
    ApiResponse createOption(Long id, CustomizeOptionCreateDTO data);
    ApiResponse getOptionList(Long itemId);
    ApiResponse getById(Long id);
    ApiResponse update(Long id, CustomizeOptionUpdateDTO data);
}
