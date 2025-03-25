package cn.dextea.product.service;

import cn.dextea.common.code.CustomizeOptionStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.option.OptionCreateDTO;
import cn.dextea.product.dto.option.OptionUpdateDTO;
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
    ApiResponse getOptionStatus(Long optionId,Long storeId);
    ApiResponse updateOptionBase(Long id, OptionUpdateDTO data) throws NotFoundException;
    ApiResponse updateOptionStatus(Long optionId, CustomizeOptionStatus status) throws NotFoundException;
    ApiResponse updateOptionStatus(Long optionId, Long storeId, CustomizeOptionStatus status);
}

