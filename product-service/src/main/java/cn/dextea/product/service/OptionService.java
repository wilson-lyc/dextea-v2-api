package cn.dextea.product.service;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.product.model.option.OptionCreateDTO;
import cn.dextea.product.model.option.OptionUpdateDTO;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface OptionService {
    ApiResponse createOption(Long itemId, OptionCreateDTO data);
    ApiResponse getOptionList(Long itemId);
    ApiResponse getOptionList(Long itemId, Long storeId);
    ApiResponse getOptionBase(Long id) throws NotFoundException;
    ApiResponse getOptionStatus(Long optionId) throws NotFoundException;
    ApiResponse getOptionStatus(Long optionId,Long storeId) throws NotFoundException;
    ApiResponse updateOptionBase(Long id, OptionUpdateDTO data) throws NotFoundException;
    ApiResponse updateOptionStatus(Long optionId, Integer status) throws NotFoundException;
    ApiResponse updateOptionStatus(Long optionId, Long storeId, Integer status);
}

