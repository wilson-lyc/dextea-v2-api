package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeItemCreateDTO;
import cn.dextea.product.dto.CustomizeItemUpdateDTO;
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
    public ApiResponse create(CustomizeItemCreateDTO data) {
        CustomizeItem customizeItem =data.toCustomize();
        // 校验ProduceId是否存在
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId,data.getProductId());
        Long num=productMapper.selectCount(wrapper);
        if(num==0){
            return ApiResponse.notFound(String.format("商品不存在，id=%d",data.getProductId()));
        }
        // 创建新的客制化项目
        customizeItemMapper.insert(customizeItem);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getList(Long productId) {
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAll(CustomizeItem.class)
                .innerJoin(Product.class,Product::getId, CustomizeItem::getProductId)
                .orderByAsc(CustomizeItem::getSort)
                .eq(Product::getId,productId);
        List<CustomizeItem> list= customizeItemMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("items",list));
    }

    @Override
    public ApiResponse getById(Long id) {
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAll(CustomizeItem.class)
                .eq(CustomizeItem::getId,id);
        CustomizeItem item = customizeItemMapper.selectOne(wrapper);
        if(item ==null){
            return ApiResponse.notFound(String.format("不存在ID=%d的客制化项目",id));
        }
        return ApiResponse.success(JSONObject.of("item", item));
    }

    @Override
    public ApiResponse update(Long id, CustomizeItemUpdateDTO data) {
        CustomizeItem customizeItem =data.toCustomize();
        customizeItem.setId(id);
        int num= customizeItemMapper.updateById(customizeItem);
        if(num==0){
            return ApiResponse.notFound(String.format("资源不存在，id=%d",id));
        }
        return ApiResponse.success("更新成功");
    }
}
