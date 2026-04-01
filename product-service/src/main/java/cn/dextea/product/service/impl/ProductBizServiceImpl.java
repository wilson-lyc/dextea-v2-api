package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.GetProductBizDetailRequest;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
import cn.dextea.product.entity.ProductCustomizationItemEntity;
import cn.dextea.product.entity.ProductCustomizationOptionEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.enums.StoreProductSaleStatus;
import cn.dextea.product.mapper.ProductCustomizationItemMapper;
import cn.dextea.product.mapper.ProductCustomizationOptionMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import cn.dextea.product.service.ProductBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductBizServiceImpl implements ProductBizService {

    private final ProductMapper productMapper;
    private final ProductCustomizationItemMapper customizationItemMapper;
    private final ProductCustomizationOptionMapper customizationOptionMapper;
    private final StoreProductStatusMapper storeProductStatusMapper;
    private final ProductConverter productConverter;
    private final CustomizationConverter customizationConverter;

    @Override
    public ApiResponse<ProductBizDetailResponse> getProductBizDetail(GetProductBizDetailRequest request) {
        Long productId = request.getProductId();
        Long storeId = request.getStoreId();

        // 查询商品信息
        ProductEntity productEntity = productMapper.selectById(productId);
        if (productEntity == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        // 校验商品全局状态：必须是上架状态
        if (productEntity.getStatus() == null || productEntity.getStatus() != ProductStatus.ENABLED.getValue()) {
            return fail(ProductErrorCode.PRODUCT_NOT_AVAILABLE);
        }

        // 查询门店商品状态
        LambdaQueryWrapper<StoreProductStatusEntity> statusQueryWrapper = new LambdaQueryWrapper<StoreProductStatusEntity>()
                .eq(StoreProductStatusEntity::getStoreId, storeId)
                .eq(StoreProductStatusEntity::getProductId, productId);
        StoreProductStatusEntity storeStatusEntity = storeProductStatusMapper.selectOne(statusQueryWrapper);

        // 校验门店商品状态：必须是在售状态
        Integer storeStatus = null;
        if (storeStatusEntity == null || storeStatusEntity.getStatus() == null
                || storeStatusEntity.getStatus() != StoreProductSaleStatus.ON_SALE.getValue()) {
            return fail(ProductErrorCode.PRODUCT_NOT_AVAILABLE);
        }
        storeStatus = storeStatusEntity.getStatus();

        // 查询商品的客制化项目列表
        LambdaQueryWrapper<ProductCustomizationItemEntity> itemQueryWrapper = new LambdaQueryWrapper<ProductCustomizationItemEntity>()
                .eq(ProductCustomizationItemEntity::getProductId, productId)
                .orderByAsc(ProductCustomizationItemEntity::getSortOrder);
        List<ProductCustomizationItemEntity> itemEntities = customizationItemMapper.selectList(itemQueryWrapper);

        // 查询每个项目下的选项
        List<CustomizationItemDetailResponse> customizationItems = itemEntities.stream()
                .map(itemEntity -> {
                    LambdaQueryWrapper<ProductCustomizationOptionEntity> optionQueryWrapper = new LambdaQueryWrapper<ProductCustomizationOptionEntity>()
                            .eq(ProductCustomizationOptionEntity::getItemId, itemEntity.getId())
                            .orderByAsc(ProductCustomizationOptionEntity::getSortOrder);
                    List<ProductCustomizationOptionEntity> optionEntities = customizationOptionMapper.selectList(optionQueryWrapper);

                    List<CustomizationOptionDetailResponse> options = optionEntities.stream()
                            .map(customizationConverter::toCustomizationOptionDetailResponse)
                            .toList();

                    return customizationConverter.toCustomizationItemDetailResponse(itemEntity, options);
                })
                .toList();

        // 构建响应数据
        ProductBizDetailResponse response = ProductBizDetailResponse.builder()
                .id(productEntity.getId())
                .name(productEntity.getName())
                .description(productEntity.getDescription())
                .price(productEntity.getPrice())
                .status(productEntity.getStatus())
                .storeStatus(storeStatus)
                .createTime(productEntity.getCreateTime())
                .updateTime(productEntity.getUpdateTime())
                .customizationItems(customizationItems)
                .build();

        return ApiResponse.success(response);
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
