package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.MenuConverter;
import cn.dextea.product.dto.request.StoreMenuQueryRequest;
import cn.dextea.product.dto.response.StoreMenuResponse;
import cn.dextea.product.entity.MenuEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreMenuBindingEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.MenuErrorCode;
import cn.dextea.product.enums.MenuStatus;
import cn.dextea.product.enums.StoreProductStatus;
import cn.dextea.product.mapper.MenuMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreMenuRelMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import cn.dextea.product.service.MenuBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuBizServiceImpl implements MenuBizService {

    private final StoreMenuRelMapper storeMenuRelMapper;
    private final MenuMapper menuMapper;
    private final ProductMapper productMapper;
    private final MenuConverter menuConverter;
    private final StoreProductStatusMapper storeProductStatusMapper;

    @Override
    public ApiResponse<StoreMenuResponse> getStoreMenu(StoreMenuQueryRequest request) {
        StoreMenuBindingEntity rel = storeMenuRelMapper.selectOne(
                new LambdaQueryWrapper<StoreMenuBindingEntity>()
                        .eq(StoreMenuBindingEntity::getStoreId, request.getStoreId()));
        if (rel == null) {
            return fail(MenuErrorCode.STORE_MENU_NOT_BOUND);
        }

        MenuEntity menu = menuMapper.selectById(rel.getMenuId());
        if (menu == null || MenuStatus.DISABLED.getValue().equals(menu.getStatus())) {
            return fail(MenuErrorCode.MENU_NOT_FOUND);
        }

        Map<Long, ProductEntity> productMap = loadProductMap(menu, request.getStoreId());
        return ApiResponse.success(menuConverter.toStoreMenuResponse(menu, productMap));
    }

    private Map<Long, ProductEntity> loadProductMap(MenuEntity menu, Long storeId) {
        if (CollectionUtils.isEmpty(menu.getGroups())) {
            return Collections.emptyMap();
        }
        List<Long> productIds = menu.getGroups().stream()
                .filter(g -> !CollectionUtils.isEmpty(g.getProductIds()))
                .flatMap(g -> g.getProductIds().stream())
                .distinct()
                .collect(Collectors.toList());
        if (productIds.isEmpty()) {
            return Collections.emptyMap();
        }
        List<ProductEntity> products = productMapper.selectBatchIds(productIds).stream()
                .toList();
        List<Long> productIds = products.stream().map(ProductEntity::getId).toList();
        Set<Long> enabledProductIds = storeProductStatusMapper.selectList(
                new LambdaQueryWrapper<StoreProductStatusEntity>()
                        .eq(StoreProductStatusEntity::getStoreId, storeId)
                        .in(StoreProductStatusEntity::getProductId, productIds))
                .stream()
                .filter(e -> Objects.equals(e.getStatus(), StoreProductStatus.ENABLED.getValue()))
                .map(StoreProductStatusEntity::getProductId)
                .collect(Collectors.toSet());
        return products.stream()
                .filter(product -> enabledProductIds.contains(product.getId()))
                .collect(Collectors.toMap(ProductEntity::getId, p -> p));
    }

    private <T> ApiResponse<T> fail(MenuErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
