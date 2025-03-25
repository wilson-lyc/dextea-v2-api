package cn.dextea.product.service.impl;

import cn.dextea.common.code.CustomizeOptionStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.product.dto.option.*;
import cn.dextea.product.mapper.ItemMapper;
import cn.dextea.product.mapper.OptionMapper;
import cn.dextea.product.mapper.OptionStatusMapper;
import cn.dextea.common.pojo.CustomizeItem;
import cn.dextea.common.pojo.CustomizeOption;
import cn.dextea.common.pojo.CustomizeOptionStoreStatus;
import cn.dextea.product.service.OptionService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
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
    public ApiResponse createOption(Long itemId, OptionCreateDTO data) {
        // 校验项目ID
        if(!productFeign.isCustomizeItemIdValid(itemId))
            throw new IllegalArgumentException("itemId错误");
        // 添加选项
        CustomizeOption option = data.toCustomizeOption();
        option.setItemId(itemId);
        option.setGlobalStatus(CustomizeOptionStatus.GLOBAL_FORBIDDEN);// 默认禁用
        optionMapper.insert(option);
        return ApiResponse.success("选项创建成功");
    }

    /**
     * 获取选项列表 - 公司
     * 只返回全局状态
     * @param itemId 项目ID
     */
    @Override
    public ApiResponse getOptionList(Long itemId) {
        // 校验项目ID
        if(!productFeign.isCustomizeItemIdValid(itemId))
            throw new IllegalArgumentException("itemId错误");
        // 查询
        MPJLambdaWrapper<CustomizeOption> wrapper = new MPJLambdaWrapper<CustomizeOption>()
                .selectAsClass(CustomizeOption.class, OptionListDTO.class)
                .eq(CustomizeOption::getItemId, itemId)
                .orderByAsc(CustomizeOption::getSort);
        List<OptionListDTO> list = optionMapper.selectJoinList(OptionListDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of(
                "count",list.size(),
                "options", list));
    }

    /**
     * 获取选项列表 - 门店
     * 返回门店状态和全局状态
     * @param itemId 项目ID
     * @param storeId 门店ID
     */
    @Override
    public ApiResponse getOptionList(Long itemId, Long storeId){
        // 校验项目ID
        if(!productFeign.isCustomizeItemIdValid(itemId))
            throw new IllegalArgumentException("itemId错误");
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId))
            throw new IllegalArgumentException("storeId错误");
        // 查询
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .selectAsClass(CustomizeOption.class, OptionListDTO.class)
                .eq(CustomizeOption::getItemId,itemId)
                // 门店状态 - 表内无记录说明可用
                .leftJoin(CustomizeOptionStoreStatus.class,"os", on -> on
                        .eq(CustomizeOptionStoreStatus::getOptionId,CustomizeOption::getId)
                        .eq(CustomizeOptionStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,1)",arg ->arg
                                .accept(CustomizeOptionStoreStatus::getStatus),
                        OptionListDTO::getStoreStatus);
        List<OptionListDTO> options=optionMapper.selectJoinList(OptionListDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("options",options));
    }

    @Override
    public ApiResponse getOptionBase(Long id) throws NotFoundException {
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .eq(CustomizeOption::getId,id)
                .selectAsClass(CustomizeOption.class, OptionBaseDTO.class);
        OptionBaseDTO option = optionMapper.selectJoinOne(OptionBaseDTO.class,wrapper);
        if (Objects.isNull(option))
            throw new NotFoundException("客制化选项不存在");
        return ApiResponse.success(JSONObject.of("option", option));
    }

    /**
     * 获取全局状态
     * @param optionId 选项ID
     */
    @Override
    public ApiResponse getOptionStatus(Long optionId) throws NotFoundException {
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .select(CustomizeOption::getGlobalStatus)
                .eq(CustomizeOption::getId,optionId);
        CustomizeOptionStatus globalStatus = optionMapper.selectJoinOne(CustomizeOptionStatus.class,wrapper);
        if (Objects.isNull(globalStatus))
            throw new NotFoundException("客制化选项不存在");
        return ApiResponse.success(JSONObject.of("globalStatus", globalStatus));
    }

    /**
     * 获取门店状态
     * @param optionId 选项ID
     * @param storeId 门店ID
     */
    @Override
    public ApiResponse getOptionStatus(Long optionId, Long storeId) {
        // 校验选项ID
        if(!productFeign.isCustomizeOptionIdValid(optionId))
            throw new IllegalArgumentException("optionId错误");
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId))
            throw new IllegalArgumentException("storeId错误");
        // 全局状态
        MPJLambdaWrapper<CustomizeOption> globalWrapper=new MPJLambdaWrapper<CustomizeOption>()
                .select(CustomizeOption::getGlobalStatus)
                .eq(CustomizeOption::getId,optionId);
        CustomizeOptionStatus globalStatus = optionMapper.selectJoinOne(CustomizeOptionStatus.class,globalWrapper);
        // 门店状态
        MPJLambdaWrapper<CustomizeOption> storeWrapper=new MPJLambdaWrapper<CustomizeOption>()
                .leftJoin(CustomizeOptionStoreStatus.class,"os", on -> on
                        .eq(CustomizeOptionStoreStatus::getOptionId,CustomizeOption::getId)
                        .eq(CustomizeOptionStoreStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,1)",arg ->arg
                                .accept(CustomizeOptionStoreStatus::getStatus),
                        OptionStatusDTO::getStoreStatus)
                .eq(CustomizeOption::getId,optionId);
        CustomizeOptionStatus storeStatus = optionMapper.selectJoinOne(CustomizeOptionStatus.class,storeWrapper);
        return ApiResponse.success(JSONObject.of(
                "globalStatus",globalStatus,
                "storeStatus",storeStatus));
    }

    /**
     * 更新选项详情
     * 可以修改全局状态
     * @param optionId 选项ID
     * @param data 数据
     */
    @Override
    public ApiResponse updateOptionBase(Long optionId,OptionUpdateDTO data) throws NotFoundException {
        CustomizeOption option=data.toCustomizeOption();
        option.setId(optionId);
        // 校验状态
        if(option.getGlobalStatus()!=CustomizeOptionStatus.GLOBAL_FORBIDDEN && option.getGlobalStatus()!=CustomizeOptionStatus.AVAILABLE)
            throw new IllegalArgumentException("客制化选项状态码有误");
        // 更新db
        if (optionMapper.updateById(option) == 0)
            throw new NotFoundException("客制化选项不存在");
        return ApiResponse.success("更新成功");
    }

    /**
     * 更新选项全局状态
     * @param optionId 选项ID
     * @param status 状态
     */
    @Override
    public ApiResponse updateOptionStatus(Long optionId, CustomizeOptionStatus status) throws NotFoundException {
        // 校验状态
        if(status!=CustomizeOptionStatus.GLOBAL_FORBIDDEN && status!=CustomizeOptionStatus.AVAILABLE)
            throw new IllegalArgumentException("客制化选项状态码有误");
        CustomizeOption option=CustomizeOption.builder()
                .id(optionId)
                .globalStatus(status)
                .build();
        if (optionMapper.updateById(option) == 0)
            throw new NotFoundException("客制化选项不存在");
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateOptionStatus(Long optionId, Long storeId, CustomizeOptionStatus status) {
        // 校验状态
        if(status==CustomizeOptionStatus.GLOBAL_FORBIDDEN)
            throw new IllegalArgumentException("客制化选项状态码有误");
        // 校验门店ID
        if(!storeFeign.isStoreIdValid(storeId))
            throw new IllegalArgumentException("storeId错误");
        // 更新db
        MPJLambdaWrapper<CustomizeOptionStoreStatus> wrapper = new MPJLambdaWrapper<CustomizeOptionStoreStatus>()
                .eq(CustomizeOptionStoreStatus::getStoreId,storeId)
                .eq(CustomizeOptionStoreStatus::getOptionId,optionId);
        CustomizeOptionStoreStatus customizeOptionStoreStatus =optionStatusMapper.selectOne(wrapper);
        // 表中无数据是可售
        if(status==CustomizeOptionStatus.AVAILABLE){
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
                UpdateWrapper<CustomizeOptionStoreStatus> updateWrapper=new UpdateWrapper<CustomizeOptionStoreStatus>()
                        .set("status",status)
                        .eq("store_id",storeId)
                        .eq("option_id",optionId);
                optionStatusMapper.update(updateWrapper);
            }
        }
        return ApiResponse.success("更新成功");
    }
}
