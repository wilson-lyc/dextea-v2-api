package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.IngredientMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomizationOptionAdminService 单元测试")
class CustomizationOptionAdminServiceImplTest {

    private static final Long ITEM_ID_TEMPERATURE = 1L;
    private static final Long ITEM_ID_SWEETNESS = 2L;
    private static final Long ITEM_ID_POMELO = 3L;
    private static final Long NON_EXISTENT_ID = 99L;
    private static final Long NON_EXISTENT_INGREDIENT_ID = 999L;

    @Mock
    private CustomizationItemMapper itemMapper;

    @Mock
    private CustomizationOptionMapper optionMapper;

    @Mock
    private IngredientMapper ingredientMapper;

    @Spy
    private CustomizationConverter customizationConverter = new CustomizationConverter();

    @InjectMocks
    private CustomizationOptionAdminServiceImpl customizationOptionAdminService;

    static Stream<Arguments> provideOptionsForYangZhiGanLu() {
        return Stream.of(
                Arguments.of(1L, "温度", "正常冰",   BigDecimal.ZERO),
                Arguments.of(1L, "温度", "少冰",     BigDecimal.ZERO),
                Arguments.of(1L, "温度", "常温",     BigDecimal.ZERO),
                Arguments.of(2L, "甜度", "标准糖",   BigDecimal.ZERO),
                Arguments.of(2L, "甜度", "120%糖",  BigDecimal.ZERO),
                Arguments.of(2L, "甜度", "70%糖",   BigDecimal.ZERO),
                Arguments.of(2L, "甜度", "50%糖",   BigDecimal.ZERO),
                Arguments.of(2L, "甜度", "30%糖",   BigDecimal.ZERO),
                Arguments.of(2L, "甜度", "不额外加糖", BigDecimal.ZERO),
                Arguments.of(3L, "红柚", "标准",      BigDecimal.ZERO),
                Arguments.of(3L, "红柚", "不加红柚粒", BigDecimal.ZERO),
                Arguments.of(3L, "红柚", "多加红柚粒", new BigDecimal("2.00"))
        );
    }

    @ParameterizedTest(name = "[{index}] 项目={1} 选项={2}（价格 {3}）- 成功创建")
    @MethodSource("provideOptionsForYangZhiGanLu")
    void createOption_allOptionsForYangZhiGanLu_success(Long itemId, String itemName,
                                                        String optionName, BigDecimal price) {
        when(itemMapper.selectById(itemId))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(itemId, itemName));
        when(optionMapper.exists(any())).thenReturn(false);
        when(optionMapper.insert(any(CustomizationOptionEntity.class))).thenAnswer(invocation -> {
            CustomizationOptionEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return 1;
        });

        CreateCustomizationOptionRequest request = CreateCustomizationOptionRequest.builder()
                .name(optionName)
                .price(price)
                .build();

        ApiResponse<CreateCustomizationOptionResponse> response =
                customizationOptionAdminService.createOption(itemId, request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getItemId()).isEqualTo(itemId);
        assertThat(response.getData().getName()).isEqualTo(optionName);
        assertThat(response.getData().getPrice()).isEqualByComparingTo(price);
        assertThat(response.getData().getStatus()).isEqualTo(CustomizationStatus.DISABLED.getValue());
    }

    @Test
    @DisplayName("创建选项 - 客制化项目不存在，返回 ITEM_NOT_FOUND 错误")
    void createOption_itemNotFound_returnsItemNotFoundError() {
        when(itemMapper.selectById(NON_EXISTENT_ID)).thenReturn(null);

        CreateCustomizationOptionRequest request = CreateCustomizationOptionRequest.builder()
                .name("正常冰")
                .price(BigDecimal.ZERO)
                .build();

        ApiResponse<CreateCustomizationOptionResponse> response =
                customizationOptionAdminService.createOption(NON_EXISTENT_ID, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.ITEM_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("创建选项 - 同一项目内选项名称重复（非禁用状态），返回 OPTION_NAME_DUPLICATE 错误")
    void createOption_duplicateOptionName_returnsOptionNameDuplicateError() {
        when(itemMapper.selectById(ITEM_ID_TEMPERATURE))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(ITEM_ID_TEMPERATURE, "温度"));
        when(optionMapper.exists(any())).thenReturn(true);

        CreateCustomizationOptionRequest request = CreateCustomizationOptionRequest.builder()
                .name("正常冰")
                .price(BigDecimal.ZERO)
                .build();

        ApiResponse<CreateCustomizationOptionResponse> response =
                customizationOptionAdminService.createOption(ITEM_ID_TEMPERATURE, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.OPTION_NAME_DUPLICATE.getCode());
    }

    @Test
    @DisplayName("创建选项 - 指定的原料不存在，返回 INGREDIENT_NOT_FOUND 错误")
    void createOption_ingredientNotFound_returnsIngredientNotFoundError() {
        when(itemMapper.selectById(ITEM_ID_POMELO))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(ITEM_ID_POMELO, "红柚"));
        when(optionMapper.exists(any())).thenReturn(false);
        when(ingredientMapper.exists(any())).thenReturn(false);

        CreateCustomizationOptionRequest request = CreateCustomizationOptionRequest.builder()
                .name("多加红柚粒")
                .price(new BigDecimal("2.00"))
                .ingredientId(NON_EXISTENT_INGREDIENT_ID)
                .ingredientQuantity(new BigDecimal("1.00"))
                .build();

        ApiResponse<CreateCustomizationOptionResponse> response =
                customizationOptionAdminService.createOption(ITEM_ID_POMELO, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.INGREDIENT_NOT_FOUND.getCode());
    }
}
