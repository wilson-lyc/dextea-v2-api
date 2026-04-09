package cn.dextea.product.service.impl;

import cn.dextea.common.util.StringValueUtils;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.MenuConverter;
import cn.dextea.product.dto.request.BindStoreMenuRequest;
import cn.dextea.product.dto.request.CreateMenuRequest;
import cn.dextea.product.dto.request.MenuGroupRequest;
import cn.dextea.product.dto.request.MenuPageQueryRequest;
import cn.dextea.product.dto.request.UpdateMenuRequest;
import cn.dextea.product.dto.response.CreateMenuResponse;
import cn.dextea.product.dto.response.MenuDetailResponse;
import cn.dextea.product.entity.MenuEntity;
import cn.dextea.product.entity.MenuGroupData;
import cn.dextea.product.entity.StoreMenuRelEntity;
import cn.dextea.product.enums.MenuErrorCode;
import cn.dextea.product.mapper.MenuMapper;
import cn.dextea.product.mapper.StoreMenuRelMapper;
import cn.dextea.product.service.MenuAdminService;
import cn.dextea.product.service.ProductCacheEvictionService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MenuAdminServiceImpl implements MenuAdminService {

    private final MenuMapper menuMapper;
    private final MenuConverter menuConverter;
    private final StoreMenuRelMapper storeMenuRelMapper;
    private final ProductCacheEvictionService cacheEvictionService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateMenuResponse> createMenu(CreateMenuRequest request) {
        String name = request.getName().trim();

        if (existsByName(name, null)) {
            return fail(MenuErrorCode.MENU_NAME_ALREADY_EXISTS);
        }

        MenuEntity entity = MenuEntity.builder()
                .name(name)
                .description(request.getDescription())
                .status(request.getStatus())
                .groups(toGroupDataList(request.getGroups()))
                .build();

        if (menuMapper.insert(entity) != 1) {
            return fail(MenuErrorCode.CREATE_FAILED);
        }

        return ApiResponse.success(menuConverter.toCreateMenuResponse(entity));
    }

    @Override
    public ApiResponse<IPage<MenuDetailResponse>> getMenuPage(MenuPageQueryRequest request) {
        LambdaQueryWrapper<MenuEntity> queryWrapper = new LambdaQueryWrapper<MenuEntity>()
                .like(StringValueUtils.hasText(request.getName()), MenuEntity::getName, StringValueUtils.trim(request.getName()))
                .eq(request.getStatus() != null, MenuEntity::getStatus, request.getStatus())
                .orderByDesc(MenuEntity::getId);

        IPage<MenuEntity> entityPage = menuMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<MenuDetailResponse> responsePage = entityPage.convert(menuConverter::toMenuDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<MenuDetailResponse> getMenuDetail(Long id) {
        MenuEntity entity = menuMapper.selectById(id);
        if (entity == null) {
            return fail(MenuErrorCode.MENU_NOT_FOUND);
        }
        return ApiResponse.success(menuConverter.toMenuDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<MenuDetailResponse> updateMenu(Long id, UpdateMenuRequest request) {
        MenuEntity entity = menuMapper.selectById(id);
        if (entity == null) {
            return fail(MenuErrorCode.MENU_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (existsByName(name, id)) {
            return fail(MenuErrorCode.MENU_NAME_ALREADY_EXISTS);
        }

        entity.setName(name);
        entity.setDescription(request.getDescription());
        entity.setStatus(request.getStatus());
        entity.setGroups(toGroupDataList(request.getGroups()));

        if (menuMapper.updateById(entity) != 1) {
            return fail(MenuErrorCode.UPDATE_FAILED);
        }

        // Menu structure changed — evict all biz menu caches (which stores are affected is unknown without querying)
        cacheEvictionService.evictMenuBizAll();

        return ApiResponse.success(menuConverter.toMenuDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteMenu(Long id) {
        MenuEntity entity = menuMapper.selectById(id);
        if (entity == null) {
            return fail(MenuErrorCode.MENU_NOT_FOUND);
        }
        if (storeMenuRelMapper.exists(new LambdaQueryWrapper<StoreMenuRelEntity>()
                .eq(StoreMenuRelEntity::getMenuId, id))) {
            return fail(MenuErrorCode.MENU_HAS_BOUND_STORES);
        }
        if (menuMapper.deleteById(id) != 1) {
            return fail(MenuErrorCode.DELETE_FAILED);
        }
        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> bindStore(Long menuId, BindStoreMenuRequest request) {
        MenuEntity menu = menuMapper.selectById(menuId);
        if (menu == null) {
            return fail(MenuErrorCode.MENU_NOT_FOUND);
        }
        if (storeMenuRelMapper.exists(new LambdaQueryWrapper<StoreMenuRelEntity>()
                .eq(StoreMenuRelEntity::getStoreId, request.getStoreId()))) {
            return fail(MenuErrorCode.STORE_ALREADY_HAS_MENU);
        }
        StoreMenuRelEntity rel = StoreMenuRelEntity.builder()
                .storeId(request.getStoreId())
                .menuId(menuId)
                .build();
        storeMenuRelMapper.insert(rel);

        cacheEvictionService.evictMenuBizByStore(request.getStoreId());

        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> unbindStore(Long menuId, Long storeId) {
        int deleted = storeMenuRelMapper.delete(new LambdaQueryWrapper<StoreMenuRelEntity>()
                .eq(StoreMenuRelEntity::getMenuId, menuId)
                .eq(StoreMenuRelEntity::getStoreId, storeId));
        if (deleted == 0) {
            return fail(MenuErrorCode.STORE_MENU_BINDING_NOT_FOUND);
        }

        cacheEvictionService.evictMenuBizByStore(storeId);

        return ApiResponse.success();
    }

    private boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<MenuEntity> queryWrapper = new LambdaQueryWrapper<MenuEntity>()
                .eq(MenuEntity::getName, name)
                .ne(excludeId != null, MenuEntity::getId, excludeId);
        return menuMapper.exists(queryWrapper);
    }

    private List<MenuGroupData> toGroupDataList(List<MenuGroupRequest> groups) {
        if (CollectionUtils.isEmpty(groups)) {
            return Collections.emptyList();
        }
        return groups.stream()
                .map(g -> MenuGroupData.builder()
                        .name(g.getName())
                        .productIds(g.getProductIds())
                        .build())
                .collect(Collectors.toList());
    }

    private <T> ApiResponse<T> fail(MenuErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
