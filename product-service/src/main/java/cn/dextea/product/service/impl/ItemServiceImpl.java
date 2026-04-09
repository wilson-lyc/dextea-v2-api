package cn.dextea.product.service.impl;

import cn.dextea.common.code.CustomizeItemStatus;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.product.CustomizeItemModel;
import cn.dextea.product.code.ProductErrorCode;
import cn.dextea.product.model.item.ItemUpdateRequest;
import cn.dextea.product.model.item.ItemCreateRequest;
import cn.dextea.product.mapper.ItemMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.CustomizeItem;
import cn.dextea.product.service.ItemService;
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
public class ItemServiceImpl implements ItemService {
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductFeign productFeign;

    @Override
    public DexteaApiResponse<Void> createItem(Long productId, ItemCreateRequest data) {
        // 校验商品ID
        if(!productFeign.isProductIdValid(productId)) {
            return DexteaApiResponse.fail(ProductErrorCode.PRODUCT_ID_ERROR.getCode(),
                    ProductErrorCode.PRODUCT_ID_ERROR.getMsg());
        }
        // 创建项目
        CustomizeItem item =data.toCustomize();
        item.setStatus(CustomizeItemStatus.FORBIDDEN.getValue());// 默认全局禁用
        item.setProductId(productId);
        itemMapper.insert(item);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<List<CustomizeItemModel>> getItemList(Long productId) {
        // 校验商品ID
        if(!productFeign.isProductIdValid(productId)) {
            return DexteaApiResponse.fail(ProductErrorCode.PRODUCT_ID_ERROR.getCode(),
                    ProductErrorCode.PRODUCT_ID_ERROR.getMsg());
        }
        // 查询数据
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAsClass(CustomizeItem.class,CustomizeItemModel.class)
                .eq(CustomizeItem::getProductId,productId)
                .orderByAsc(CustomizeItem::getSort);
        List<CustomizeItemModel> list= itemMapper.selectJoinList(CustomizeItemModel.class,wrapper);
        return DexteaApiResponse.success(list);
    }

    @Override
    public DexteaApiResponse<CustomizeItemModel> getItemDetail(Long id){
        MPJLambdaWrapper<CustomizeItem> wrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAsClass(CustomizeItem.class,CustomizeItemModel.class)
                .eq(CustomizeItem::getId,id);
        CustomizeItemModel item = itemMapper.selectJoinOne(CustomizeItemModel.class,wrapper);
        if(Objects.isNull(item)){
            return DexteaApiResponse.notFound(ProductErrorCode.CUSTOMIZE_ITEM_NOT_FOUND.getCode(),
                    ProductErrorCode.CUSTOMIZE_ITEM_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(item);
    }

    @Override
    public DexteaApiResponse<Void> updateItemDetail(Long id, ItemUpdateRequest data){
        CustomizeItem customizeItem =data.toCustomize();
        customizeItem.setId(id);
        if(itemMapper.updateById(customizeItem)==0){
            return DexteaApiResponse.notFound(ProductErrorCode.CUSTOMIZE_ITEM_NOT_FOUND.getCode(),
                    ProductErrorCode.CUSTOMIZE_ITEM_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> updateItemStatus(Long id, Integer status){
        LambdaUpdateWrapper<CustomizeItem> wrapper=new LambdaUpdateWrapper<CustomizeItem>()
                .eq(CustomizeItem::getId,id)
                .set(CustomizeItem::getStatus,status);
        if(itemMapper.update(wrapper)==0){
            return DexteaApiResponse.notFound(ProductErrorCode.CUSTOMIZE_ITEM_NOT_FOUND.getCode(),
                    ProductErrorCode.CUSTOMIZE_ITEM_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }
}
