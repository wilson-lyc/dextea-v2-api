package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomizationItemAdminService 单元测试")
class CustomizationItemAdminServiceImplTest {

    @Mock
    private CustomizationItemMapper itemMapper;

    @Spy
    private CustomizationConverter customizationConverter = new CustomizationConverter();

    @InjectMocks
    private CustomizationItemAdminServiceImpl customizationItemAdminService;

    @ParameterizedTest(name = "创建杨枝甘露客制化项目 [{index}] {0} - 成功")
    @CsvSource({
        "温度, 饮品温度选择",
        "甜度, 饮品甜度选择",
        "红柚, 红柚粒添加选项"
    })
    void create_yangZhiGanLuCustomizationItems_success(String name, String description) {
        CreateCustomizationItemRequest request = CreateCustomizationItemRequest.builder()
                .name(name)
                .description(description)
                .build();

        when(itemMapper.exists(any())).thenReturn(false);
        when(itemMapper.insert(any(CustomizationItemEntity.class))).thenAnswer(invocation -> {
            CustomizationItemEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return 1;
        });

        ApiResponse<CreateCustomizationItemResponse> response = customizationItemAdminService.create(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getName()).isEqualTo(name);
        assertThat(response.getData().getDescription()).isEqualTo(description);
        assertThat(response.getData().getStatus()).isEqualTo(CustomizationStatus.DISABLED.getValue());
    }

    @Test
    @DisplayName("创建客制化项目 - 名称与已激活项目重复，返回 ITEM_NAME_DUPLICATE 错误")
    void create_duplicateActiveItemName_returnsItemNameDuplicateError() {
        CreateCustomizationItemRequest request = CreateCustomizationItemRequest.builder()
                .name("温度")
                .build();

        when(itemMapper.exists(any())).thenReturn(true);

        ApiResponse<CreateCustomizationItemResponse> response = customizationItemAdminService.create(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.ITEM_NAME_DUPLICATE.getCode());
    }

    @Test
    @DisplayName("创建客制化项目 - 数据库写入失败，返回 ITEM_CREATE_FAILED 错误")
    void create_mapperInsertFails_returnsItemCreateFailedError() {
        CreateCustomizationItemRequest request = CreateCustomizationItemRequest.builder()
                .name("温度")
                .build();

        when(itemMapper.exists(any())).thenReturn(false);
        when(itemMapper.insert(any(CustomizationItemEntity.class))).thenReturn(0);

        ApiResponse<CreateCustomizationItemResponse> response = customizationItemAdminService.create(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.ITEM_CREATE_FAILED.getCode());
    }
}
