package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CreateOptionRequest;
import cn.dextea.product.dto.request.UpdateOptionStatusRequest;
import cn.dextea.product.dto.request.UpdateOptionInfoRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.OptionDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.IngredientEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.IngredientStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.IngredientMapper;
import cn.dextea.product.service.CustomizationOptionAdminService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationOptionAdminServiceImpl implements CustomizationOptionAdminService {

    private final CustomizationItemMapper itemMapper;
    private final CustomizationOptionMapper optionMapper;
    private final IngredientMapper ingredientMapper;
    private final CustomizationConverter customizationConverter;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<CreateCustomizationOptionResponse> createOption(Long itemId,
                                                                       CreateOptionRequest request) {
        CustomizationItemEntity item = itemMapper.selectById(itemId);
        if (item == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (optionNameExistsInItem(itemId, name, null)) {
            return fail(CustomizationErrorCode.OPTION_NAME_DUPLICATE);
        }

        if (request.getIngredientId() != null) {
            if (!ingredientExists(request.getIngredientId())) {
                return fail(CustomizationErrorCode.INGREDIENT_NOT_FOUND);
            }
        }

        CustomizationOptionEntity entity = CustomizationOptionEntity.builder()
                .itemId(itemId)
                .name(name)
                .price(request.getPrice())
                .ingredientId(request.getIngredientId())
                .ingredientQuantity(request.getIngredientQuantity())
                .status(CustomizationStatus.DISABLED.getValue())
                .build();

        if (optionMapper.insert(entity) != 1) {
            return fail(CustomizationErrorCode.OPTION_CREATE_FAILED);
        }

        return ApiResponse.success(customizationConverter.toCreateOptionResponse(entity));
    }

    @Override
    public ApiResponse<List<OptionDetailResponse>> getItemOptionsList(Long itemId) {
        CustomizationItemEntity item = itemMapper.selectById(itemId);
        if (item == null) {
            return fail(CustomizationErrorCode.ITEM_NOT_FOUND);
        }

        List<CustomizationOptionEntity> options = optionMapper.selectList(
                new LambdaQueryWrapper<CustomizationOptionEntity>()
                        .eq(CustomizationOptionEntity::getItemId, itemId)
                        .orderByAsc(CustomizationOptionEntity::getId));

        return ApiResponse.success(options.stream()
                .map(customizationConverter::toOptionDetailResponse)
                .collect(Collectors.toList()));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<OptionDetailResponse> updateOptionInfo(Long id,
                                                              UpdateOptionInfoRequest request) {
        CustomizationOptionEntity entity = optionMapper.selectById(id);
        if (entity == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        String name = request.getName().trim();
        if (optionNameExistsInItem(entity.getItemId(), name, id)) {
            return fail(CustomizationErrorCode.OPTION_NAME_DUPLICATE);
        }

        if (request.getIngredientId() != null) {
            if (!ingredientExists(request.getIngredientId())) {
                return fail(CustomizationErrorCode.INGREDIENT_NOT_FOUND);
            }
        }

        entity.setName(name);
        entity.setPrice(request.getPrice());
        entity.setIngredientId(request.getIngredientId());
        entity.setIngredientQuantity(request.getIngredientQuantity());
        if (optionMapper.updateById(entity) != 1) {
            return fail(CustomizationErrorCode.OPTION_UPDATE_FAILED);
        }

        return ApiResponse.success(customizationConverter.toOptionDetailResponse(entity));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> updateOptionStatus(Long id, UpdateOptionStatusRequest request) {
        CustomizationOptionEntity entity = optionMapper.selectById(id);
        if (entity == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }

        entity.setStatus(request.getStatus());
        if (optionMapper.updateById(entity) != 1) {
            return fail(CustomizationErrorCode.OPTION_UPDATE_FAILED);
        }

        return ApiResponse.success();
    }

    @Override
    public ApiResponse<OptionDetailResponse> getOptionDetail(Long id) {
        CustomizationOptionEntity entity = optionMapper.selectById(id);
        if (entity == null) {
            return fail(CustomizationErrorCode.OPTION_NOT_FOUND);
        }
        return ApiResponse.success(customizationConverter.toOptionDetailResponse(entity));
    }

    private boolean optionNameExistsInItem(Long itemId, String name, Long excludeId) {
        return optionMapper.exists(new LambdaQueryWrapper<CustomizationOptionEntity>()
                .eq(CustomizationOptionEntity::getItemId, itemId)
                .eq(CustomizationOptionEntity::getName, name)
                .ne(CustomizationOptionEntity::getStatus, CustomizationStatus.DISABLED.getValue())
                .ne(excludeId != null, CustomizationOptionEntity::getId, excludeId));
    }

    private boolean ingredientExists(Long ingredientId) {
        return ingredientMapper.exists(new LambdaQueryWrapper<IngredientEntity>()
                .eq(IngredientEntity::getId, ingredientId)
                .eq(IngredientEntity::getStatus, IngredientStatus.ACTIVE.getValue()));
    }

    private <T> ApiResponse<T> fail(CustomizationErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
