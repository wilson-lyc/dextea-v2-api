package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.product.dto.item.ItemUpdateDTO;
import cn.dextea.product.dto.item.ItemCreateDTO;
import cn.dextea.product.mapper.ItemMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.common.pojo.CustomizeItem;
import cn.dextea.product.service.ItemService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class ItemServiceImpl implements ItemService {
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductFeign productFeign;

    @Override
    public ApiResponse createItem(Long productId, @Valid ItemCreateDTO data) {
        // 校验商品ID
        if(!productFeign.isProductIdValid(productId)) {
            return ApiResponse.notFound("商品ID有误");
        }
        // 创建项目
        CustomizeItem item =data.toCustomize();
        item.setStatus(0);// 默认禁用
        item.setProductId(productId);
        // 写入db
        itemMapper.insert(item);
        return ApiResponse.success("项目创建成功");
    }

    @Override
    public ApiResponse getItemList(Long productId) {
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAll(CustomizeItem.class)
                .eq(CustomizeItem::getProductId,productId)
                .orderByAsc(CustomizeItem::getSort);
        List<CustomizeItem> list= itemMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("items",list));
    }

    @Override
    public ApiResponse getItemInfo(Long id) {
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAll(CustomizeItem.class)
                .eq(CustomizeItem::getId,id);
        CustomizeItem item = itemMapper.selectOne(wrapper);
        if( item == null ){
            return ApiResponse.notFound(String.format("不存在id=%d的客制化项目", id));
        }
        return ApiResponse.success(JSONObject.of("item", item));
    }

    @Override
    public ApiResponse updateItemInfo(Long id, ItemUpdateDTO data) {
        CustomizeItem customizeItem =data.toCustomize();
        customizeItem.setId(id);
        if(itemMapper.updateById(customizeItem)==0){
            return ApiResponse.notFound(String.format("不存在id=%d的客制化项目", id));
        }
        return ApiResponse.success("更新成功");
    }
}
