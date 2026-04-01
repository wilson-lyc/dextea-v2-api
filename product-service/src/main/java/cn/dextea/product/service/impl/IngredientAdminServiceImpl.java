package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.IngredientConverter;
import cn.dextea.product.dto.request.CreateIngredientRequest;
import cn.dextea.product.dto.request.IngredientPageQueryRequest;
import cn.dextea.product.dto.request.UpdateIngredientRequest;
import cn.dextea.product.dto.response.CreateIngredientResponse;
import cn.dextea.product.dto.response.IngredientDetailResponse;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.enums.IngredientErrorCode;
import cn.dextea.product.enums.IngredientStatus;
import cn.dextea.product.mapper.IngredientMapper;
import cn.dextea.product.service.IngredientAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class IngredientAdminServiceImpl implements IngredientAdminService {

    private final IngredientMapper ingredientMapper;
    private final IngredientConverter ingredientConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateIngredientResponse> createIngredient(CreateIngredientRequest request) {
        String name = request.getName().trim();

        if (existsByName(name, null)) {
            return fail(IngredientErrorCode.NAME_ALREADY_EXISTS);
        }

        IngredientEntity entity = IngredientEntity.builder()
                .name(name)
                .shelfLife(request.getShelfLife())
                .shelfLifeUnit(request.getShelfLifeUnit())
                .storageMethod(request.getStorageMethod())
                .status(IngredientStatus.ACTIVE.getValue())
                .build();

        if (ingredientMapper.insert(entity) != 1) {
            return fail(IngredientErrorCode.CREATE_FAILED);
        }

        return ApiResponse.success(ingredientConverter.toCreateIngredientResponse(entity));
    }

    @Override
    public ApiResponse<IPage<IngredientDetailResponse>> getIngredientPage(IngredientPageQueryRequest request) {
        LambdaQueryWrapper<IngredientEntity> queryWrapper = new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue())
                .like(hasText(request.getName()), IngredientEntity::getName, trim(request.getName()))
                .orderByDesc(IngredientEntity::getId);

        IPage<IngredientEntity> entityPage = ingredientMapper.selectPage(
                new Page<>(request.getCurrent(), request.getSize()), queryWrapper);
        IPage<IngredientDetailResponse> responsePage = entityPage.convert(ingredientConverter::toIngredientDetailResponse);
        return ApiResponse.success(responsePage);
    }

    @Override
    public ApiResponse<IngredientDetailResponse> getIngredientDetail(Long id) {
        IngredientEntity entity = getActiveById(id);
        if (entity == null) {
            return fail(IngredientErrorCode.INGREDIENT_NOT_FOUND);
        }
        return ApiResponse.success(ingredientConverter.toIngredientDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<IngredientDetailResponse> updateIngredient(Long id, UpdateIngredientRequest request) {
        IngredientEntity entity = getActiveById(id);
        if (entity == null) {
            return fail(IngredientErrorCode.INGREDIENT_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (existsByName(name, id)) {
            return fail(IngredientErrorCode.NAME_ALREADY_EXISTS);
        }

        entity.setName(name);
        entity.setShelfLife(request.getShelfLife());
        entity.setShelfLifeUnit(request.getShelfLifeUnit());
        entity.setStorageMethod(request.getStorageMethod());

        if (ingredientMapper.updateById(entity) != 1) {
            return fail(IngredientErrorCode.UPDATE_FAILED);
        }

        return ApiResponse.success(ingredientConverter.toIngredientDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteIngredient(Long id) {
        IngredientEntity entity = getActiveById(id);
        if (entity == null) {
            return fail(IngredientErrorCode.INGREDIENT_NOT_FOUND);
        }

        entity.setStatus(IngredientStatus.DELETED.getValue());
        ingredientMapper.updateById(entity);
        return ApiResponse.success();
    }

    // ---- Helpers ----

    private IngredientEntity getActiveById(Long id) {
        LambdaQueryWrapper<IngredientEntity> queryWrapper = new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getId, id)
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue());
        return ingredientMapper.selectOne(queryWrapper);
    }

    private boolean existsByName(String name, Long excludeId) {
        LambdaQueryWrapper<IngredientEntity> queryWrapper = new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getName, name)
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue())
                .ne(excludeId != null, IngredientEntity::getId, excludeId);
        return ingredientMapper.exists(queryWrapper);
    }

    private <T> ApiResponse<T> fail(IngredientErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private String trim(String value) {
        return value == null ? null : value.trim();
    }
}
