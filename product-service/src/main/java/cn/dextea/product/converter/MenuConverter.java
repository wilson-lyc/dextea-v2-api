package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateMenuResponse;
import cn.dextea.product.dto.response.MenuDetailResponse;
import cn.dextea.product.dto.response.MenuGroupResponse;
import cn.dextea.product.dto.response.MenuProductItemResponse;
import cn.dextea.product.dto.response.StoreMenuGroupResponse;
import cn.dextea.product.dto.response.StoreMenuResponse;
import cn.dextea.product.entity.MenuEntity;
import cn.dextea.product.entity.MenuGroupEntity;
import cn.dextea.product.entity.ProductEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class MenuConverter {

    public CreateMenuResponse toCreateMenuResponse(MenuEntity entity) {
        return CreateMenuResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    public MenuDetailResponse toMenuDetailResponse(MenuEntity entity) {
        return MenuDetailResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .status(entity.getStatus())
                .groups(toMenuGroupResponseList(entity.getGroups()))
                .createTime(entity.getCreateTime())
                .updateTime(entity.getUpdateTime())
                .build();
    }

    public StoreMenuResponse toStoreMenuResponse(MenuEntity entity, Map<Long, ProductEntity> productMap) {
        List<StoreMenuGroupResponse> groups = CollectionUtils.isEmpty(entity.getGroups())
                ? Collections.emptyList()
                : entity.getGroups().stream()
                        .map(g -> StoreMenuGroupResponse.builder()
                                .name(g.getName())
                                .products(toMenuProductItemList(g.getProductIds(), productMap))
                                .build())
                        .collect(Collectors.toList());
        return StoreMenuResponse.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .groups(groups)
                .build();
    }

    private List<MenuProductItemResponse> toMenuProductItemList(List<Long> productIds, Map<Long, ProductEntity> productMap) {
        if (CollectionUtils.isEmpty(productIds)) {
            return Collections.emptyList();
        }
        return productIds.stream()
                .map(productMap::get)
                .filter(p -> p != null)
                .map(p -> MenuProductItemResponse.builder()
                        .id(p.getId())
                        .name(p.getName())
                        .description(p.getDescription())
                        .price(p.getPrice())
                        .status(p.getStatus())
                        .build())
                .collect(Collectors.toList());
    }

    private List<MenuGroupResponse> toMenuGroupResponseList(List<MenuGroupEntity> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return Collections.emptyList();
        }
        return groups.stream()
                .map(g -> MenuGroupResponse.builder()
                        .name(g.getName())
                        .productIds(g.getProductIds())
                        .build())
                .collect(Collectors.toList());
    }
}
