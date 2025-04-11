package cn.dextea.product.service.impl;

import cn.dextea.common.code.CustomizeOptionStatus;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.CustomizeOptionModel;
import cn.dextea.product.code.ProductErrorCode;
import cn.dextea.product.mapper.OptionMapper;
import cn.dextea.product.mapper.OptionStatusMapper;
import cn.dextea.product.model.option.OptionCreateRequest;
import cn.dextea.product.model.option.OptionUpdateRequest;
import cn.dextea.product.pojo.CustomizeOption;
import cn.dextea.product.pojo.CustomizeOptionStoreStatus;
import cn.dextea.product.service.OptionService;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class OptionServiceImpl implements OptionService {
    @Resource
    private OptionMapper optionMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private StoreFeign storeFeign;
    @Resource
    private OptionStatusMapper optionStatusMapper;

    @Override
    public DexteaApiResponse<Void> createOption(Long itemId, OptionCreateRequest data) {
        // 校验项目ID
        if(!productFeign.isCustomizeItemIdValid(itemId)) {
            return DexteaApiResponse.fail(ProductErrorCode.CUSTOMIZE_ITEM_ID_ERROR.getCode(),
                    ProductErrorCode.CUSTOMIZE_ITEM_ID_ERROR.getMsg());
        }
        // 添加选项
        CustomizeOption option = data.toCustomizeOption(itemId);
        optionMapper.insert(option);
        return DexteaApiResponse.success();
    }

    // 获取选项列表 - 仅公司状态
    @Override
    public DexteaApiResponse<List<CustomizeOptionModel>> getOptionList(Long itemId) {
        // 校验项目ID
        if(!productFeign.isCustomizeItemIdValid(itemId)){
            return DexteaApiResponse.fail(ProductErrorCode.CUSTOMIZE_ITEM_ID_ERROR.getCode(),
                    ProductErrorCode.CUSTOMIZE_ITEM_ID_ERROR.getMsg());
        }
        // 查询
        MPJLambdaWrapper<CustomizeOption> wrapper = new MPJLambdaWrapper<CustomizeOption>()
                .selectAsClass(CustomizeOption.class, CustomizeOptionModel.class)
                .eq(CustomizeOption::getItemId, itemId)
                .orderByAsc(CustomizeOption::getSort);
        List<CustomizeOptionModel> list = optionMapper.selectJoinList(CustomizeOptionModel.class,wrapper);
        return DexteaApiResponse.success(list);
    }

    // 获取选项列表 - 全局+门店状态
    @Override
    public DexteaApiResponse<List<CustomizeOptionModel>> getOptionList(Long itemId, Long storeId){
        // 校验项目ID
        if(!productFeign.isCustomizeItemIdValid(itemId)){
            return DexteaApiResponse.fail(ProductErrorCode.CUSTOMIZE_ITEM_ID_ERROR.getCode(),
                    ProductErrorCode.CUSTOMIZE_ITEM_ID_ERROR.getMsg());
        }
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId)) {
            return DexteaApiResponse.fail(ProductErrorCode.STORE_ID_ERROR.getCode(),
                    ProductErrorCode.STORE_ID_ERROR.getMsg());
        }
        // 查询
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .selectAsClass(CustomizeOption.class, CustomizeOptionModel.class)
                .eq(CustomizeOption::getItemId,itemId)
                // 门店状态 - 表内无记录说明可用
                .leftJoin(CustomizeOptionStoreStatus.class,"os", on -> on
                        .eq(CustomizeOptionStoreStatus::getOptionId,CustomizeOption::getId)
                        .eq(CustomizeOptionStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,1)",arg ->arg
                                .accept(CustomizeOptionStoreStatus::getStatus),
                        CustomizeOptionModel::getStoreStatus);
        List<CustomizeOptionModel> list=optionMapper.selectJoinList(CustomizeOptionModel.class,wrapper);
        return DexteaApiResponse.success(list);
    }

    @Override
    public DexteaApiResponse<CustomizeOptionModel> getOptionBase(Long id){
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .eq(CustomizeOption::getId,id)
                .selectAsClass(CustomizeOption.class, CustomizeOptionModel.class);
        CustomizeOptionModel option = optionMapper.selectJoinOne(CustomizeOptionModel.class,wrapper);
        if (Objects.isNull(option)) {
            return DexteaApiResponse.notFound(ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getCode(),
                    ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(option);
    }

    // 获取全局状态
    @Override
    public DexteaApiResponse<CustomizeOptionModel> getOptionStatus(Long optionId){
        Integer globalStatus= productFeign.getOptionGlobalStatus(optionId);
        if (Objects.isNull(globalStatus)) {
            return DexteaApiResponse.notFound(ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getCode(),
                    ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getMsg());
        }
        CustomizeOptionModel status=CustomizeOptionModel.builder()
                .globalStatus(globalStatus)
                .build();
        return DexteaApiResponse.success(status);
    }

    // 获取门店状态
    @Override
    public DexteaApiResponse<CustomizeOptionModel> getOptionStatus(Long optionId, Long storeId){
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId)) {
            return DexteaApiResponse.fail(ProductErrorCode.STORE_ID_ERROR.getCode(),
                    ProductErrorCode.STORE_ID_ERROR.getMsg());
        }
        // 全局状态
        Integer globalStatus= productFeign.getOptionGlobalStatus(optionId);
        if (Objects.isNull(globalStatus)) {
            return DexteaApiResponse.notFound(ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getCode(),
                    ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getMsg());
        }
        // 门店状态
        Integer storeStatus= productFeign.getOptionStoreStatus(optionId,storeId);
        CustomizeOptionModel status=CustomizeOptionModel.builder()
                .globalStatus(globalStatus)
                .storeStatus(storeStatus)
                .build();
        return DexteaApiResponse.success(status);
    }

    @Override
    public DexteaApiResponse<Void> updateOptionBase(Long optionId, OptionUpdateRequest data){
        CustomizeOption option=data.toCustomizeOption(optionId);
        // 校验状态
        if(option.getGlobalStatus()!= CustomizeOptionStatus.GLOBAL_FORBIDDEN.getValue() &&
                option.getGlobalStatus()!=CustomizeOptionStatus.AVAILABLE.getValue()) {
            return DexteaApiResponse.fail(ProductErrorCode.GLOBAL_STATUS_ERROR.getCode(),
                    ProductErrorCode.GLOBAL_STATUS_ERROR.getMsg());
        }
        // 更新db
        if (optionMapper.updateById(option) == 0) {
            return DexteaApiResponse.notFound(ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getCode(),
                    ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }

    // 更新选项全局状态
    @Override
    public DexteaApiResponse<Void> updateOptionStatus(Long optionId, Integer status){
        // 校验状态
        if(status!=CustomizeOptionStatus.GLOBAL_FORBIDDEN.getValue() && status!=CustomizeOptionStatus.AVAILABLE.getValue()){
            return DexteaApiResponse.fail(ProductErrorCode.GLOBAL_STATUS_ERROR.getCode(),
                    ProductErrorCode.GLOBAL_STATUS_ERROR.getMsg());
        }
        LambdaUpdateWrapper<CustomizeOption> wrapper=new LambdaUpdateWrapper<CustomizeOption>()
                .eq(CustomizeOption::getId,optionId)
                .set(CustomizeOption::getGlobalStatus,status);
        if (optionMapper.update(wrapper) == 0) {
            return DexteaApiResponse.notFound(ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getCode(),
                    ProductErrorCode.CUSTOMIZE_OPTION_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> updateOptionStatus(Long optionId, Long storeId, Integer status) {
        // 校验状态
        if(status==CustomizeOptionStatus.GLOBAL_FORBIDDEN.getValue()) {
            return DexteaApiResponse.fail(ProductErrorCode.GLOBAL_STATUS_ERROR.getCode(),
                    ProductErrorCode.GLOBAL_STATUS_ERROR.getMsg());
        }
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId)) {
            return DexteaApiResponse.fail(ProductErrorCode.STORE_ID_ERROR.getCode(),
                    ProductErrorCode.STORE_ID_ERROR.getMsg());
        }
        // 更新db
        MPJLambdaWrapper<CustomizeOptionStoreStatus> wrapper = new MPJLambdaWrapper<CustomizeOptionStoreStatus>()
                .eq(CustomizeOptionStoreStatus::getStoreId,storeId)
                .eq(CustomizeOptionStoreStatus::getOptionId,optionId);
        CustomizeOptionStoreStatus customizeOptionStoreStatus =optionStatusMapper.selectOne(wrapper);
        // 表中无数据是可售
        if(status==CustomizeOptionStatus.AVAILABLE.getValue()){
            if(Objects.nonNull(customizeOptionStoreStatus)){
                optionStatusMapper.delete(wrapper);
            }
        }else{
            if(Objects.isNull(customizeOptionStoreStatus)){
                CustomizeOptionStoreStatus status1 = CustomizeOptionStoreStatus.builder()
                        .storeId(storeId)
                        .optionId(optionId)
                        .status(status)
                        .build();
                optionStatusMapper.insert(status1);
            }else {
                LambdaUpdateWrapper<CustomizeOptionStoreStatus> updateWrapper=new LambdaUpdateWrapper<CustomizeOptionStoreStatus>()
                        .set(CustomizeOptionStoreStatus::getStatus,status)
                        .eq(CustomizeOptionStoreStatus::getStoreId,storeId)
                        .eq(CustomizeOptionStoreStatus::getOptionId,optionId);
                optionStatusMapper.update(updateWrapper);
            }
        }
        return DexteaApiResponse.success();
    }
}
