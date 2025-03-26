package cn.dextea.product.service.impl;

import cn.dextea.common.code.CustomizeItemStatus;
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
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    public ApiResponse createItem(Long productId,ItemCreateDTO data) {
        // 校验商品ID
        if(!productFeign.isProductIdValid(productId)) {
            throw new IllegalArgumentException("productId错误");
        }
        // 创建项目
        CustomizeItem item =data.toCustomize();
        item.setProductId(productId);
        itemMapper.insert(item);
        return ApiResponse.success("项目创建成功",JSONObject.of("item",item));
    }

    @Override
    public ApiResponse getItemList(Long productId) {
        // 校验商品ID
        if(!productFeign.isProductIdValid(productId)) {
            throw new IllegalArgumentException("productId错误");
        }
        // 查询数据
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAll(CustomizeItem.class)
                .eq(CustomizeItem::getProductId,productId)
                .orderByAsc(CustomizeItem::getSort);
        List<CustomizeItem> list= itemMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("items",list));
    }

    @Override
    public ApiResponse getItemInfo(Long id) throws NotFoundException {
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAll(CustomizeItem.class)
                .eq(CustomizeItem::getId,id);
        CustomizeItem item = itemMapper.selectOne(wrapper);
        if(Objects.isNull(item))
            throw new NotFoundException("客制化项目不存在");
        return ApiResponse.success(JSONObject.of("item", item));
    }

    @Override
    public ApiResponse updateItemInfo(Long id, ItemUpdateDTO data) throws NotFoundException {
        CustomizeItem customizeItem =data.toCustomize();
        customizeItem.setId(id);
        if(itemMapper.updateById(customizeItem)==0)
            throw new NotFoundException("客制化项目不存在");
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateItemStatus(Long id, Integer status) throws NotFoundException {
        CustomizeItem customizeItem = CustomizeItem.builder()
                .id(id)
                .status(status)
                .build();
        if(itemMapper.updateById(customizeItem)==0)
            throw new NotFoundException("客制化项目不存在");
        return ApiResponse.success("状态更新成功");
    }
}
