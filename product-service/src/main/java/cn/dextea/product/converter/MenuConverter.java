package cn.dextea.product.converter;

import cn.dextea.product.dto.response.CreateMenuResponse;
import cn.dextea.product.dto.response.MenuDetailResponse;
import cn.dextea.product.dto.response.MenuGroupResponse;
import cn.dextea.product.entity.MenuEntity;
import cn.dextea.product.entity.MenuGroupData;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
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

    private List<MenuGroupResponse> toMenuGroupResponseList(List<MenuGroupData> groups) {
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
