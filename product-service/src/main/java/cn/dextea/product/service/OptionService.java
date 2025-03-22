package cn.dextea.product.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.option.OptionCreateDTO;
import cn.dextea.product.dto.option.OptionUpdateDTO;

/**
 * @author Lai Yongchao
 */
public interface OptionService {
    ApiResponse createOption(Long itemId, OptionCreateDTO data);
    ApiResponse getOptionList(Long itemId);
    ApiResponse getOptionList(Long itemId, Long storeId);
    ApiResponse getOptionBase(Long id);
    ApiResponse getOptionStatus(Long optionId);
    ApiResponse getOptionStatus(Long optionId,Long storeId);
    ApiResponse updateOptionBase(Long id, OptionUpdateDTO data);
    ApiResponse updateOptionStatus(Long optionId, Integer status);
    ApiResponse updateOptionStatus(Long optionId, Long storeId, Integer status);
}

