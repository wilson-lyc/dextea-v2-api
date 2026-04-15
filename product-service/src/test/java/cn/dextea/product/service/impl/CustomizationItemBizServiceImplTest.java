package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.UpdateStoreCustomizationItemStatusRequest;
import cn.dextea.product.entity.StoreCustomizationItemStatusEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.StoreCustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.StoreCustomizationItemStatusMapper;
import org.junit.jupiter.api.BeforeAll;
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
@DisplayName("CustomizationItemBizService - 客制化项目门店状态单元测试")
class CustomizationItemBizServiceImplTest {

    private static final Long STORE_ID = 10L;
    private static final Long ITEM_ID_TEMPERATURE = 1L;
    private static final Long ITEM_ID_SWEETNESS = 2L;
    private static final Long ITEM_ID_POMELO = 3L;
    private static final Long NON_EXISTENT_ID = 999L;

    @BeforeAll
    static void initMybatisPlusLambdaCache() {
        MybatisPlusTestSupport.initTableInfo(StoreCustomizationItemStatusEntity.class);
    }

    @Mock
    private CustomizationItemMapper itemMapper;

    @Mock
    private StoreCustomizationItemStatusMapper storeItemStatusMapper;

    @Spy
    private CustomizationConverter customizationConverter = new CustomizationConverter();

    @InjectMocks
    private CustomizationItemBizServiceImpl customizationItemBizService;

    @ParameterizedTest(name = "首次为门店启用客制化项目 [{index}] ID={0} {1} - 插入新记录")
    @CsvSource({
        "1, 温度",
        "2, 甜度",
        "3, 红柚"
    })
    void updateStatus_firstTime_insertsNewStatusRecord(Long itemId, String itemName) {
        when(itemMapper.selectById(itemId))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(itemId, itemName));
        when(storeItemStatusMapper.selectOne(any())).thenReturn(null);
        when(storeItemStatusMapper.insert(any(StoreCustomizationItemStatusEntity.class))).thenReturn(1);

        UpdateStoreCustomizationItemStatusRequest request = UpdateStoreCustomizationItemStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response = customizationItemBizService.updateStatus(itemId, request);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("门店已有温度客制化项目状态 - 更新为售罄")
    void updateStatus_existingRecord_updatesStatusToDisabled() {
        StoreCustomizationItemStatusEntity existing = StoreCustomizationItemStatusEntity.builder()
                .storeId(STORE_ID)
                .itemId(ITEM_ID_TEMPERATURE)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        when(itemMapper.selectById(ITEM_ID_TEMPERATURE))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(ITEM_ID_TEMPERATURE, "温度"));
        when(storeItemStatusMapper.selectOne(any())).thenReturn(existing);
        when(storeItemStatusMapper.update(any(), any())).thenReturn(1);

        UpdateStoreCustomizationItemStatusRequest request = UpdateStoreCustomizationItemStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.DISABLED.getValue())
                .build();

        ApiResponse<Void> response = customizationItemBizService.updateStatus(ITEM_ID_TEMPERATURE, request);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("设置门店客制化项目状态 - 项目不存在，返回 ITEM_NOT_FOUND 错误")
    void updateStatus_itemNotFound_returnsItemNotFoundError() {
        when(itemMapper.selectById(NON_EXISTENT_ID)).thenReturn(null);

        UpdateStoreCustomizationItemStatusRequest request = UpdateStoreCustomizationItemStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response = customizationItemBizService.updateStatus(NON_EXISTENT_ID, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.ITEM_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("首次设置门店客制化项目状态 - 数据库插入失败，返回 STORE_ITEM_SALE_STATUS_UPDATE_FAILED 错误")
    void updateStatus_insertFails_returnsStoreItemStatusUpdateFailedError() {
        when(itemMapper.selectById(ITEM_ID_SWEETNESS))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(ITEM_ID_SWEETNESS, "甜度"));
        when(storeItemStatusMapper.selectOne(any())).thenReturn(null);
        when(storeItemStatusMapper.insert(any(StoreCustomizationItemStatusEntity.class))).thenReturn(0);

        UpdateStoreCustomizationItemStatusRequest request = UpdateStoreCustomizationItemStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response = customizationItemBizService.updateStatus(ITEM_ID_SWEETNESS, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.STORE_ITEM_SALE_STATUS_UPDATE_FAILED.getCode());
    }

    @Test
    @DisplayName("更新已有门店客制化项目状态 - 数据库更新失败，返回 STORE_ITEM_SALE_STATUS_UPDATE_FAILED 错误")
    void updateStatus_updateFails_returnsStoreItemStatusUpdateFailedError() {
        StoreCustomizationItemStatusEntity existing = StoreCustomizationItemStatusEntity.builder()
                .storeId(STORE_ID)
                .itemId(ITEM_ID_POMELO)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        when(itemMapper.selectById(ITEM_ID_POMELO))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(ITEM_ID_POMELO, "红柚"));
        when(storeItemStatusMapper.selectOne(any())).thenReturn(existing);
        when(storeItemStatusMapper.update(any(), any())).thenReturn(0);

        UpdateStoreCustomizationItemStatusRequest request = UpdateStoreCustomizationItemStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.DISABLED.getValue())
                .build();

        ApiResponse<Void> response = customizationItemBizService.updateStatus(ITEM_ID_POMELO, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.STORE_ITEM_SALE_STATUS_UPDATE_FAILED.getCode());
    }
}
