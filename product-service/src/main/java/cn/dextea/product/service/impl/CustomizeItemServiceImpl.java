package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeItemEditDTO;
import cn.dextea.product.dto.CustomizeItemDTO;
import cn.dextea.product.mapper.CustomizeItemMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.CustomizeItem;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.service.CustomizeItemService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomizeItemServiceImpl implements CustomizeItemService {
    @Resource
    private CustomizeItemMapper customizeItemMapper;
    @Resource
    private ProductMapper productMapper;

    @Override
    public ApiResponse createItem(Long id, CustomizeItemEditDTO data) {
        CustomizeItem customizeItem =data.toCustomize();
        customizeItem.setGlobalStatus(1);// 全局状态默认禁用
        customizeItem.setProductId(id);
        // 校验ProduceId是否存在
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId,id);
        if(!productMapper.exists(wrapper)){
            return ApiResponse.notFound(String.format("不存在id=%d的商品",id));
        }
        // 写入db
        customizeItemMapper.insert(customizeItem);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getItemList(Long id) {
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAsClass(CustomizeItem.class, CustomizeItemDTO.class)
                .orderByAsc(CustomizeItem::getSort)
                .eq(CustomizeItem::getProductId,id);
        List<CustomizeItem> list= customizeItemMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("items",list));
    }

    @Override
    public ApiResponse getItemBase(Long id) {
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAsClass(CustomizeItem.class,CustomizeItemDTO.class)
                .eq(CustomizeItem::getId,id);
        CustomizeItem item = customizeItemMapper.selectOne(wrapper);
        if(item ==null){
            return ApiResponse.notFound("无符合条件的客制化项目");
        }
        return ApiResponse.success(JSONObject.of("item", item));
    }

    @Override
    public ApiResponse getItemGlobalStatus(Long id) {
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAsClass(CustomizeItem.class,CustomizeItemDTO.class)
                .eq(CustomizeItem::getId,id);
        CustomizeItem item = customizeItemMapper.selectOne(wrapper);
        if(item ==null){
            return ApiResponse.notFound("无符合条件的客制化项目");
        }
        return ApiResponse.success(JSONObject.of("status", item.getGlobalStatus()));
    }

    @Override
    public ApiResponse updateItemBase(Long id, CustomizeItemEditDTO data) {
        CustomizeItem customizeItem =data.toCustomize();
        customizeItem.setId(id);
        int num= customizeItemMapper.updateById(customizeItem);
        if(num==0){
            return ApiResponse.notFound("无符合条件的客制化项目");
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateItemStatus(Long id, Integer status) {
        CustomizeItem customizeItem=CustomizeItem.builder()
                .id(id)
                .globalStatus(status)
                .build();
        int num= customizeItemMapper.updateById(customizeItem);
        if(num==0){
            return ApiResponse.notFound("无符合条件的客制化项目");
        }
        return ApiResponse.success("更新成功");
    }
}
