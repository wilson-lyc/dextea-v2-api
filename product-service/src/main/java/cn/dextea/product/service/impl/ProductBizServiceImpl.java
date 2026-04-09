package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.ProductPageQueryWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreProductSaleRequest;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreProductRelEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.enums.StoreProductSaleStatus;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreProductRelMapper;
import cn.dextea.product.service.ProductBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductBizServiceImpl implements ProductBizService {

    private final ProductMapper productMapper;
    private final StoreProductRelMapper storeProductRelMapper;
    private final ProductConverter productConverter;

    @Override
    public ApiResponse<IPage<ProductDetailResponse>> getProductPage(ProductPageQueryWithStoreIdRequest request) {
        Long storeId = request.getStoreId();

        // 分页查询全局上架的商品
        LambdaQueryWrapper<ProductEntity> productQuery = new LambdaQueryWrapper<ProductEntity>()
                .eq(ProductEntity::getStatus, ProductStatus.ENABLED.getValue())
                .like(StringValueUtils.hasText(request.getName()), ProductEntity::getName, request.getName().trim())
                .orderByDesc(ProductEntity::getId);
        IPage<ProductEntity> productPage = productMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), productQuery);

        List<ProductEntity> products = productPage.getRecords();
        if (products.isEmpty()) {
            return ApiResponse.success(productPage.convert(
                    p -> productConverter.toProductDetailResponseWithStoreStatus(p, StoreProductSaleStatus.SOLD_OUT.getValue())));
        }

        // 批量查询这批商品在门店中的在售关联记录，存在即在售
        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        LambdaQueryWrapper<StoreProductRelEntity> relQuery = new LambdaQueryWrapper<StoreProductRelEntity>()
                .eq(StoreProductRelEntity::getStoreId, storeId)
                .in(StoreProductRelEntity::getProductId, productIds);
        Set<Long> onSaleProductIds = storeProductRelMapper.selectList(relQuery).stream()
                .map(StoreProductRelEntity::getProductId)
                .collect(Collectors.toSet());

        return ApiResponse.success(productPage.convert(entity -> {
            int storeStatus = onSaleProductIds.contains(entity.getId())
                    ? StoreProductSaleStatus.ON_SALE.getValue()
                    : StoreProductSaleStatus.SOLD_OUT.getValue();
            return productConverter.toProductDetailResponseWithStoreStatus(entity, storeStatus);
        }));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateSaleStatus(Long productId, UpdateStoreProductSaleRequest request) {
        ProductEntity product = productMapper.selectById(productId);
        if (product == null) {
            return fail(ProductErrorCode.PRODUCT_NOT_FOUND);
        }

        Long storeId = request.getStoreId();
        LambdaQueryWrapper<StoreProductRelEntity> relQuery = new LambdaQueryWrapper<StoreProductRelEntity>()
                .eq(StoreProductRelEntity::getStoreId, storeId)
                .eq(StoreProductRelEntity::getProductId, productId);

        if (Boolean.TRUE.equals(request.getOnSale())) {
            // 标记在售：插入关联记录（幂等，已存在则跳过）
            if (!storeProductRelMapper.exists(relQuery)) {
                StoreProductRelEntity rel = StoreProductRelEntity.builder()
                        .storeId(storeId)
                        .productId(productId)
                        .build();
                if (storeProductRelMapper.insert(rel) != 1) {
                    return fail(ProductErrorCode.STORE_SALE_STATUS_UPDATE_FAILED);
                }
            }
        } else {
            // 标记售罄：删除关联记录
            storeProductRelMapper.delete(relQuery);
        }

        return ApiResponse.success();
    }

    private <T> ApiResponse<T> fail(ProductErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
