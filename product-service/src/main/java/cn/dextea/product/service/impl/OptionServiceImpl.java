package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.option.*;
import cn.dextea.product.feign.ProductFeign;
import cn.dextea.product.feign.StoreFeign;
import cn.dextea.product.mapper.ItemMapper;
import cn.dextea.product.mapper.OptionMapper;
import cn.dextea.product.mapper.OptionStatusMapper;
import cn.dextea.product.pojo.CustomizeItem;
import cn.dextea.product.pojo.CustomizeOption;
import cn.dextea.product.pojo.OptionStatus;
import cn.dextea.product.service.OptionService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
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
    private ItemMapper itemMapper;
    @Resource
    private ProductFeign productFeign;
    @Resource
    private StoreFeign storeFeign;
    @Resource
    private OptionStatusMapper optionStatusMapper;

    @Override
    public ApiResponse createOption(Long itemId, OptionCreateDTO data) {
        // 校验项目ID
        if(!productFeign.isCustomizeItemIdValid(itemId)){
            return ApiResponse.badRequest("客制化项目ID有误");
        }
        // 添加客制化选项
        CustomizeOption option = data.toCustomizeOption();
        option.setItemId(itemId);// 绑定项目
        option.setGlobalStatus(0);// 默认禁用
        // 插入db
        optionMapper.insert(option);
        return ApiResponse.success("选项创建成功");
    }

    @Override
    public ApiResponse getOptionList(Long itemId) {
        // 校验ID有效
        if(!productFeign.isCustomizeItemIdValid(itemId)){
            return ApiResponse.badRequest("请求参数错误");
        }
        // 查询db
        MPJLambdaWrapper<CustomizeOption> wrapper = new MPJLambdaWrapper<CustomizeOption>()
                .selectAsClass(CustomizeOption.class, OptionListDTO.class)
                .leftJoin(CustomizeItem.class, CustomizeItem::getId,CustomizeOption::getItemId)
                .eq(CustomizeItem::getId, itemId)
                .orderByAsc(CustomizeOption::getSort);
        List<OptionListDTO> list = optionMapper.selectJoinList(OptionListDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("options", list));
    }

    @Override
    public ApiResponse getOptionList(Long itemId, Long storeId){
        // 校验ID有效
        if(!productFeign.isCustomizeItemIdValid(itemId)||!storeFeign.isStoreIdValid(storeId)){
            return ApiResponse.badRequest("请求参数错误");
        }
        // 查询db
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .selectAsClass(CustomizeOption.class, OptionListDTO.class)
                .eq(CustomizeOption::getItemId,itemId)
                // 门店状态 - 表内无记录说明可用
                .leftJoin(OptionStatus.class,"os", on -> on
                        .eq(OptionStatus::getOptionId,CustomizeOption::getId)
                        .eq(OptionStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,1)",arg ->arg
                                .accept(OptionStatus::getStatus),
                        OptionListDTO::getStoreStatus);
        List<OptionListDTO> options=optionMapper.selectJoinList(OptionListDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("options",options));
    }

    @Override
    public ApiResponse getOptionBase(Long id) {
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .eq(CustomizeOption::getId,id)
                .selectAsClass(CustomizeOption.class, OptionBaseDTO.class)
                // 项目
                .leftJoin(CustomizeItem.class,CustomizeItem::getId,CustomizeOption::getItemId)
                .selectAs(CustomizeItem::getName,OptionBaseDTO::getItemName);
        OptionBaseDTO option = optionMapper.selectJoinOne(OptionBaseDTO.class,wrapper);
        if (option == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的客制化选项", id));
        }
        return ApiResponse.success(JSONObject.of("option", option));
    }


    @Override
    public ApiResponse getOptionStatus(Long optionId) {
        MPJLambdaWrapper<CustomizeOption> wrapper=new MPJLambdaWrapper<CustomizeOption>()
                .selectAsClass(CustomizeOption.class, OptionStatusDTO.class)
                .eq(CustomizeOption::getId,optionId);
        OptionStatusDTO option = optionMapper.selectJoinOne(OptionStatusDTO.class,wrapper);
        if (option == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的客制化选项", optionId));
        }
        return ApiResponse.success(JSONObject.of("status", option));
    }

    @Override
    public ApiResponse getOptionStatus(Long optionId, Long storeId) {
        // 校验ID有效
        if(!productFeign.isCustomizeOptionIdValid(optionId)||!storeFeign.isStoreIdValid(storeId)){
            return ApiResponse.badRequest("请求参数错误");
        }
        MPJLambdaWrapper<CustomizeOption> wrapper = new MPJLambdaWrapper<CustomizeOption>()
                // 全局状态
                .selectAs(CustomizeOption::getGlobalStatus, OptionStatusDTO::getGlobalStatus)
                // 门店状态
                .leftJoin(OptionStatus.class,"os", on -> on
                        .eq(OptionStatus::getOptionId,CustomizeOption::getId)
                        .eq(OptionStatus::getStoreId,storeId))
                .selectFunc("coalesce(%s,1)",arg ->arg
                                .accept(OptionStatus::getStatus),
                        OptionStatusDTO::getStoreStatus)
                .eq(CustomizeOption::getId,optionId);
        OptionStatusDTO status=optionMapper.selectJoinOne(OptionStatusDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("status",status));
    }

    @Override
    public ApiResponse updateOptionBase(Long id,OptionUpdateDTO data) {
        CustomizeOption option=data.toCustomizeOption();
        option.setId(id);
        // 校验全局状态
        if(option.getGlobalStatus()<0||option.getGlobalStatus()>1){
            return ApiResponse.badRequest("状态码有误");
        }
        // 更新db
        if (optionMapper.updateById(option) == 0) {
            return ApiResponse.notFound(String.format("不存在id=%d的客制化选项", id));
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateOptionStatus(Long optionId, Integer status) {
        if(status<0||status>1){
            return ApiResponse.badRequest("状态码有误");
        }
        CustomizeOption option=CustomizeOption.builder()
                .id(optionId)
                .globalStatus(status)
                .build();
        if (optionMapper.updateById(option) == 0) {
            return ApiResponse.notFound(String.format("不存在id=%d的客制化选项", optionId));
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateOptionStatus(Long optionId, Long storeId, Integer status) {
        // 校验ID
        if (!storeFeign.isStoreIdValid(storeId)||!productFeign.isCustomizeOptionIdValid(optionId)){
            return ApiResponse.badRequest("请求参数错误");
        }
        // 校验status
        if (status<1||status>2){
            return ApiResponse.badRequest("状态码错误");
        }
        // 更新db
        MPJLambdaWrapper<OptionStatus> wrapper = new MPJLambdaWrapper<OptionStatus>()
                .eq(OptionStatus::getStoreId,storeId)
                .eq(OptionStatus::getOptionId,optionId);
        OptionStatus optionStatus=optionStatusMapper.selectOne(wrapper);
        if(status==1){
            if(Objects.nonNull(optionStatus)){
                optionStatusMapper.delete(wrapper);
            }
        }else{
            if(Objects.isNull(optionStatus)){
                OptionStatus status1=OptionStatus.builder()
                        .storeId(storeId)
                        .optionId(optionId)
                        .status(status)
                        .build();
                optionStatusMapper.insert(status1);
            }else {
                UpdateWrapper<OptionStatus> updateWrapper=new UpdateWrapper<OptionStatus>()
                        .set("status",status)
                        .eq("store_id",storeId)
                        .eq("option_id",optionId);
                optionStatusMapper.update(updateWrapper);
            }
        }
        return ApiResponse.success("更新成功");
    }
}
