package cn.dextea.product.service;


import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.CustomizeOptionModel;
import cn.dextea.product.model.option.OptionCreateRequest;
import cn.dextea.product.model.option.OptionUpdateRequest;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface OptionService {
    DexteaApiResponse<Void> createOption(Long itemId, OptionCreateRequest data);
    DexteaApiResponse<List<CustomizeOptionModel>> getOptionList(Long itemId);
    DexteaApiResponse<List<CustomizeOptionModel>> getOptionList(Long itemId, Long storeId);
    DexteaApiResponse<CustomizeOptionModel> getOptionBase(Long id) throws NotFoundException;
    DexteaApiResponse<CustomizeOptionModel> getOptionStatus(Long optionId) throws NotFoundException;
    DexteaApiResponse<CustomizeOptionModel> getOptionStatus(Long optionId, Long storeId) throws NotFoundException;
    DexteaApiResponse<Void> updateOptionBase(Long id, OptionUpdateRequest data) throws NotFoundException;
    DexteaApiResponse<Void> updateOptionStatus(Long optionId, Integer status) throws NotFoundException;
    DexteaApiResponse<Void> updateOptionStatus(Long optionId, Long storeId, Integer status);
}

