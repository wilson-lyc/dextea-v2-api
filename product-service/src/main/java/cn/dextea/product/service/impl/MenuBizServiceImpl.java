package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.cache.CacheNames;
import cn.dextea.product.converter.MenuConverter;
import cn.dextea.product.dto.request.StoreMenuQueryRequest;
import cn.dextea.product.dto.response.StoreMenuResponse;
import cn.dextea.product.entity.MenuEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreMenuBindingEntity;
import cn.dextea.product.enums.MenuErrorCode;
import cn.dextea.product.enums.MenuStatus;
import cn.dextea.product.mapper.MenuMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreMenuRelMapper;
import cn.dextea.product.service.MenuBizService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuBizServiceImpl implements MenuBizService {

    private final StoreMenuRelMapper storeMenuRelMapper;
    private final MenuMapper menuMapper;
    private final ProductMapper productMapper;
    private final MenuConverter menuConverter;

    @Override
    @Cacheable(
            cacheNames = CacheNames.MENU_BIZ,
            key = "'store:' + #request.storeId",
            unless = "#result.code != 0"
    )
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

        Map<Long, ProductEntity> productMap = loadProductMap(menu);
        return ApiResponse.success(menuConverter.toStoreMenuResponse(menu, productMap));
    }

    private Map<Long, ProductEntity> loadProductMap(MenuEntity menu) {
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
        return productMapper.selectBatchIds(productIds).stream()
                .collect(Collectors.toMap(ProductEntity::getId, p -> p));
    }

    private <T> ApiResponse<T> fail(MenuErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
