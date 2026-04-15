package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.dto.request.ItemOptionsListInStore;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionStatusRequest;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.StoreCustomizationOptionStatusEntity;
import cn.dextea.product.enums.CustomizationErrorCode;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.StoreCustomizationStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionStatusMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomizationOptionBizService - 客制化选项门店状态单元测试")
class CustomizationOptionBizServiceImplTest {

    private static final Long STORE_ID = 10L;
    private static final Long ITEM_ID_POMELO = 3L;
    private static final Long OPTION_ID_STANDARD = 7L;
    private static final Long OPTION_ID_NO_POMELO = 8L;
    private static final Long OPTION_ID_EXTRA_POMELO = 9L;
    private static final Long NON_EXISTENT_ID = 999L;

    @BeforeAll
    static void initMybatisPlusLambdaCache() {
        MybatisPlusTestSupport.initTableInfo(StoreCustomizationOptionStatusEntity.class);
    }

    @Mock
    private CustomizationItemMapper itemMapper;

    @Mock
    private CustomizationOptionMapper optionMapper;

    @Mock
    private StoreCustomizationOptionStatusMapper storeOptionStatusMapper;

    @Spy
    private CustomizationConverter customizationConverter = new CustomizationConverter();

    @InjectMocks
    private CustomizationOptionBizServiceImpl customizationOptionBizService;

    private CustomizationOptionEntity buildOption(Long id, Long itemId, String name, BigDecimal price) {
        return CustomizationOptionEntity.builder()
                .id(id)
                .itemId(itemId)
                .name(name)
                .price(price)
                .status(CustomizationStatus.ACTIVE.getValue())
                .build();
    }

    @Test
    @DisplayName("查询红柚选项列表 - 有门店状态记录，正确回填各选项的门店状态")
    void getItemOptionsList_pomoloOptions_fillsStoreStatusCorrectly() {
        List<CustomizationOptionEntity> options = List.of(
                buildOption(OPTION_ID_STANDARD, ITEM_ID_POMELO, "标准", BigDecimal.ZERO),
                buildOption(OPTION_ID_NO_POMELO, ITEM_ID_POMELO, "不加红柚粒", BigDecimal.ZERO),
                buildOption(OPTION_ID_EXTRA_POMELO, ITEM_ID_POMELO, "多加红柚粒", new BigDecimal("2.00"))
        );
        List<StoreCustomizationOptionStatusEntity> storeStatuses = List.of(
                StoreCustomizationOptionStatusEntity.builder()
                        .storeId(STORE_ID).optionId(OPTION_ID_STANDARD)
                        .status(StoreCustomizationStatus.ENABLED.getValue()).build(),
                StoreCustomizationOptionStatusEntity.builder()
                        .storeId(STORE_ID).optionId(OPTION_ID_EXTRA_POMELO)
                        .status(StoreCustomizationStatus.ENABLED.getValue()).build()
        );

        when(itemMapper.selectById(ITEM_ID_POMELO))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(ITEM_ID_POMELO, "红柚"));
        when(optionMapper.selectList(any())).thenReturn(options);
        when(storeOptionStatusMapper.selectList(any())).thenReturn(storeStatuses);

        ItemOptionsListInStore request = ItemOptionsListInStore.builder().storeId(STORE_ID).build();

        ApiResponse<List<CustomizationOptionDetailResponse>> response =
                customizationOptionBizService.getItemOptionsList(ITEM_ID_POMELO, request);

        assertThat(response.isSuccess()).isTrue();
        List<CustomizationOptionDetailResponse> data = response.getData();
        assertThat(data).hasSize(3);

        CustomizationOptionDetailResponse standard = data.stream()
                .filter(r -> "标准".equals(r.getName())).findFirst().orElseThrow();
        assertThat(standard.getStoreStatus()).isEqualTo(StoreCustomizationStatus.ENABLED.getValue());

        CustomizationOptionDetailResponse noPomelo = data.stream()
                .filter(r -> "不加红柚粒".equals(r.getName())).findFirst().orElseThrow();
        assertThat(noPomelo.getStoreStatus()).isEqualTo(StoreCustomizationStatus.DISABLED.getValue());

        CustomizationOptionDetailResponse extraPomelo = data.stream()
                .filter(r -> "多加红柚粒".equals(r.getName())).findFirst().orElseThrow();
        assertThat(extraPomelo.getPrice()).isEqualByComparingTo(new BigDecimal("2.00"));
        assertThat(extraPomelo.getStoreStatus()).isEqualTo(StoreCustomizationStatus.ENABLED.getValue());
    }

    @Test
    @DisplayName("查询选项列表 - 无任何门店状态记录，所有选项默认为售罄")
    void getItemOptionsList_noStoreStatusRecords_allOptionsDefaultToDisabled() {
        List<CustomizationOptionEntity> options = List.of(
                buildOption(OPTION_ID_STANDARD, ITEM_ID_POMELO, "标准", BigDecimal.ZERO),
                buildOption(OPTION_ID_NO_POMELO, ITEM_ID_POMELO, "不加红柚粒", BigDecimal.ZERO)
        );

        when(itemMapper.selectById(ITEM_ID_POMELO))
                .thenReturn(CustomizationTestFixtures.buildActiveItem(ITEM_ID_POMELO, "红柚"));
        when(optionMapper.selectList(any())).thenReturn(options);
        when(storeOptionStatusMapper.selectList(any())).thenReturn(List.of());

        ItemOptionsListInStore request = ItemOptionsListInStore.builder().storeId(STORE_ID).build();

        ApiResponse<List<CustomizationOptionDetailResponse>> response =
                customizationOptionBizService.getItemOptionsList(ITEM_ID_POMELO, request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).allMatch(
                opt -> StoreCustomizationStatus.DISABLED.getValue() == opt.getStoreStatus());
    }

    @Test
    @DisplayName("查询选项列表 - 客制化项目不存在，返回 ITEM_NOT_FOUND 错误")
    void getItemOptionsList_itemNotFound_returnsItemNotFoundError() {
        when(itemMapper.selectById(NON_EXISTENT_ID)).thenReturn(null);

        ItemOptionsListInStore request = ItemOptionsListInStore.builder().storeId(STORE_ID).build();

        ApiResponse<List<CustomizationOptionDetailResponse>> response =
                customizationOptionBizService.getItemOptionsList(NON_EXISTENT_ID, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.ITEM_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("首次为门店设置多加红柚粒选项状态 - 插入新记录，设为在售")
    void updateOptionStoreStatus_firstTime_insertsNewStatusRecord() {
        when(optionMapper.selectById(OPTION_ID_EXTRA_POMELO))
                .thenReturn(buildOption(OPTION_ID_EXTRA_POMELO, ITEM_ID_POMELO, "多加红柚粒", new BigDecimal("2.00")));
        when(storeOptionStatusMapper.selectOne(any())).thenReturn(null);
        when(storeOptionStatusMapper.insert(any(StoreCustomizationOptionStatusEntity.class))).thenReturn(1);

        UpdateStoreCustomizationOptionStatusRequest request = UpdateStoreCustomizationOptionStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response =
                customizationOptionBizService.updateOptionStoreStatus(OPTION_ID_EXTRA_POMELO, request);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("门店已有选项状态记录 - 更新为售罄")
    void updateOptionStoreStatus_existingRecord_updatesStatusToDisabled() {
        StoreCustomizationOptionStatusEntity existing = StoreCustomizationOptionStatusEntity.builder()
                .storeId(STORE_ID)
                .optionId(OPTION_ID_STANDARD)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        when(optionMapper.selectById(OPTION_ID_STANDARD))
                .thenReturn(buildOption(OPTION_ID_STANDARD, ITEM_ID_POMELO, "标准", BigDecimal.ZERO));
        when(storeOptionStatusMapper.selectOne(any())).thenReturn(existing);
        when(storeOptionStatusMapper.update(any(), any())).thenReturn(1);

        UpdateStoreCustomizationOptionStatusRequest request = UpdateStoreCustomizationOptionStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.DISABLED.getValue())
                .build();

        ApiResponse<Void> response =
                customizationOptionBizService.updateOptionStoreStatus(OPTION_ID_STANDARD, request);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("设置门店选项状态 - 选项不存在，返回 OPTION_NOT_FOUND 错误")
    void updateOptionStoreStatus_optionNotFound_returnsOptionNotFoundError() {
        when(optionMapper.selectById(NON_EXISTENT_ID)).thenReturn(null);

        UpdateStoreCustomizationOptionStatusRequest request = UpdateStoreCustomizationOptionStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response = customizationOptionBizService.updateOptionStoreStatus(NON_EXISTENT_ID, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.OPTION_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("首次设置门店选项状态 - 数据库插入失败，返回 STORE_OPTION_SALE_STATUS_UPDATE_FAILED 错误")
    void updateOptionStoreStatus_insertFails_returnsStoreOptionStatusUpdateFailedError() {
        when(optionMapper.selectById(OPTION_ID_NO_POMELO))
                .thenReturn(buildOption(OPTION_ID_NO_POMELO, ITEM_ID_POMELO, "不加红柚粒", BigDecimal.ZERO));
        when(storeOptionStatusMapper.selectOne(any())).thenReturn(null);
        when(storeOptionStatusMapper.insert(any(StoreCustomizationOptionStatusEntity.class))).thenReturn(0);

        UpdateStoreCustomizationOptionStatusRequest request = UpdateStoreCustomizationOptionStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response =
                customizationOptionBizService.updateOptionStoreStatus(OPTION_ID_NO_POMELO, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(CustomizationErrorCode.STORE_OPTION_SALE_STATUS_UPDATE_FAILED.getCode());
    }
}
