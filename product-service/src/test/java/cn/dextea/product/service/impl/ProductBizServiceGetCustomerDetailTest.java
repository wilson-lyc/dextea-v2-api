package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.CustomizationConverter;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.response.CustomerProductDetailResponse;
import cn.dextea.product.entity.CustomizationItemEntity;
import cn.dextea.product.entity.CustomizationOptionEntity;
import cn.dextea.product.entity.ProductCustomizationItemBindingEntity;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreCustomizationItemStatusEntity;
import cn.dextea.product.entity.StoreCustomizationOptionStatusEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.CustomizationStatus;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.enums.StoreCustomizationStatus;
import cn.dextea.product.enums.StoreProductStatus;
import cn.dextea.product.mapper.CustomizationItemMapper;
import cn.dextea.product.mapper.CustomizationOptionMapper;
import cn.dextea.product.mapper.ProductCustomizationItemBindingMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreCustomizationItemStatusMapper;
import cn.dextea.product.mapper.StoreCustomizationOptionStatusMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
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
@DisplayName("ProductBizService - 顾客端获取商品详情单元测试")
class ProductBizServiceGetCustomerDetailTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long STORE_ID = 10L;
    private static final Long NON_EXISTENT_ID = 999L;
    private static final Long ITEM_ID_SIZE = 100L;
    private static final Long ITEM_ID_SWEETNESS = 200L;
    private static final Long OPTION_ID_LARGE = 1001L;
    private static final Long OPTION_ID_MEDIUM = 1002L;

    @Mock
    private ProductMapper productMapper;
    @Mock
    private StoreProductStatusMapper storeProductStatusMapper;
    @Mock
    private ProductCustomizationItemBindingMapper bindingMapper;
    @Mock
    private CustomizationItemMapper customizationItemMapper;
    @Mock
    private StoreCustomizationItemStatusMapper storeItemStatusMapper;
    @Mock
    private CustomizationOptionMapper customizationOptionMapper;
    @Mock
    private StoreCustomizationOptionStatusMapper storeOptionStatusMapper;

    @Spy
    private ProductConverter productConverter = new ProductConverter();
    @Spy
    private CustomizationConverter customizationConverter = new CustomizationConverter();

    @InjectMocks
    private ProductBizServiceImpl productBizService;

    private ProductEntity buildEnabledProduct() {
        return ProductEntity.builder()
                .id(PRODUCT_ID)
                .name("杨枝甘露")
                .description("好喝到爆")
                .price(new BigDecimal("28.00"))
                .status(ProductStatus.ENABLED.getValue())
                .build();
    }

    private ProductEntity buildDisabledProduct() {
        return ProductEntity.builder()
                .id(PRODUCT_ID)
                .name("杨枝甘露")
                .price(new BigDecimal("28.00"))
                .status(ProductStatus.DISABLED.getValue())
                .build();
    }

    private CustomizationItemEntity buildActiveItem(Long id, String name) {
        return CustomizationItemEntity.builder()
                .id(id)
                .name(name)
                .description(name + "描述")
                .status(CustomizationStatus.ACTIVE.getValue())
                .build();
    }

    private CustomizationItemEntity buildDisabledItem(Long id, String name) {
        return CustomizationItemEntity.builder()
                .id(id)
                .name(name)
                .status(CustomizationStatus.DISABLED.getValue())
                .build();
    }

    private CustomizationOptionEntity buildActiveOption(Long id, Long itemId, String name) {
        return CustomizationOptionEntity.builder()
                .id(id)
                .itemId(itemId)
                .name(name)
                .price(new BigDecimal("2.00"))
                .status(CustomizationStatus.ACTIVE.getValue())
                .build();
    }

    private ProductCustomizationItemBindingEntity buildBinding(Long itemId, int sortOrder) {
        return ProductCustomizationItemBindingEntity.builder()
                .productId(PRODUCT_ID)
                .itemId(itemId)
                .sortOrder(sortOrder)
                .build();
    }

    // ──────────────────────────────────────────────
    // 商品不存在 / 全局下架 校验
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("商品不存在 - 返回 PRODUCT_NOT_FOUND 错误")
    void getCustomerDetail_productNotFound_returnsProductNotFoundError() {
        when(productMapper.selectById(NON_EXISTENT_ID)).thenReturn(null);

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(NON_EXISTENT_ID, STORE_ID);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("商品全局状态为下架 - 返回 PRODUCT_DISABLED 错误")
    void getCustomerDetail_productDisabled_returnsProductDisabledError() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildDisabledProduct());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ProductErrorCode.PRODUCT_DISABLED.getCode());
    }

    // ──────────────────────────────────────────────
    // 商品门店状态回填
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("门店无商品状态记录 - 商品门店状态默认为售罄")
    void getCustomerDetail_noStoreProductStatus_defaultsToDisabled() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getStoreStatus()).isEqualTo(StoreProductStatus.DISABLED.getValue());
    }

    @Test
    @DisplayName("门店有商品状态记录 - 如实返回门店状态")
    void getCustomerDetail_storeProductStatusExists_returnsActualStatus() {
        StoreProductStatusEntity statusEntity = StoreProductStatusEntity.builder()
                .storeId(STORE_ID)
                .productId(PRODUCT_ID)
                .status(StoreProductStatus.ENABLED.getValue())
                .build();

        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(statusEntity);
        when(bindingMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getStoreStatus()).isEqualTo(StoreProductStatus.ENABLED.getValue());
    }

    // ──────────────────────────────────────────────
    // 客制化项目过滤
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("商品无绑定客制化项目 - 返回空列表")
    void getCustomerDetail_noCustomizationBindings_returnsEmptyList() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getCustomizationItems()).isEmpty();
    }

    @Test
    @DisplayName("绑定项目全部为禁用状态 - 不返回任何客制化项目")
    void getCustomerDetail_allItemsDisabled_returnsEmptyList() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(
                List.of(buildBinding(ITEM_ID_SIZE, 1)));
        // 模拟 customizationItemMapper 按 globalStatus=ACTIVE 查询后返回空（项目为禁用）
        when(customizationItemMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getCustomizationItems()).isEmpty();
    }

    @Test
    @DisplayName("仅激活的客制化项目被返回")
    void getCustomerDetail_mixedItemStatus_returnsOnlyActiveItems() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(List.of(
                buildBinding(ITEM_ID_SIZE, 1),
                buildBinding(ITEM_ID_SWEETNESS, 2)));
        // mapper 按 ACTIVE 过滤后仅返回 SIZE（SWEETNESS 被禁用，不会从 DB 返回）
        when(customizationItemMapper.selectList(any())).thenReturn(
                List.of(buildActiveItem(ITEM_ID_SIZE, "规格")));
        when(storeItemStatusMapper.selectList(any())).thenReturn(List.of());
        when(customizationOptionMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getCustomizationItems()).hasSize(1);
        assertThat(response.getData().getCustomizationItems().get(0).getId()).isEqualTo(ITEM_ID_SIZE);
    }

    @Test
    @DisplayName("客制化项目按绑定排序顺序返回")
    void getCustomerDetail_itemsReturnedInBindingSortOrder() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        // 绑定中 SWEETNESS 排序靠前
        when(bindingMapper.selectList(any())).thenReturn(List.of(
                buildBinding(ITEM_ID_SIZE, 2),
                buildBinding(ITEM_ID_SWEETNESS, 1)));
        // mapper 返回顺序随机（未按 sortOrder）
        when(customizationItemMapper.selectList(any())).thenReturn(List.of(
                buildActiveItem(ITEM_ID_SIZE, "规格"),
                buildActiveItem(ITEM_ID_SWEETNESS, "甜度")));
        when(storeItemStatusMapper.selectList(any())).thenReturn(List.of());
        when(customizationOptionMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        var items = response.getData().getCustomizationItems();
        assertThat(items).hasSize(2);
        assertThat(items.get(0).getId()).isEqualTo(ITEM_ID_SWEETNESS);
        assertThat(items.get(1).getId()).isEqualTo(ITEM_ID_SIZE);
    }

    // ──────────────────────────────────────────────
    // 客制化选项过滤
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("仅激活的客制化选项被返回")
    void getCustomerDetail_mixedOptionStatus_returnsOnlyActiveOptions() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(
                List.of(buildBinding(ITEM_ID_SIZE, 1)));
        when(customizationItemMapper.selectList(any())).thenReturn(
                List.of(buildActiveItem(ITEM_ID_SIZE, "规格")));
        when(storeItemStatusMapper.selectList(any())).thenReturn(List.of());
        // mapper 按 ACTIVE 过滤后仅返回 LARGE（MEDIUM 被禁用，不会从 DB 返回）
        when(customizationOptionMapper.selectList(any())).thenReturn(
                List.of(buildActiveOption(OPTION_ID_LARGE, ITEM_ID_SIZE, "大杯")));
        when(storeOptionStatusMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        var items = response.getData().getCustomizationItems();
        assertThat(items).hasSize(1);
        assertThat(items.get(0).getOptions()).hasSize(1);
        assertThat(items.get(0).getOptions().get(0).getId()).isEqualTo(OPTION_ID_LARGE);
    }

    // ──────────────────────────────────────────────
    // 客制化项目和选项门店状态回填
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("客制化项目无门店状态记录 - 项目门店状态默认为售罄")
    void getCustomerDetail_noItemStoreStatus_defaultsToDisabled() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(
                List.of(buildBinding(ITEM_ID_SIZE, 1)));
        when(customizationItemMapper.selectList(any())).thenReturn(
                List.of(buildActiveItem(ITEM_ID_SIZE, "规格")));
        when(storeItemStatusMapper.selectList(any())).thenReturn(List.of());
        when(customizationOptionMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getCustomizationItems().get(0).getStoreStatus())
                .isEqualTo(StoreCustomizationStatus.DISABLED.getValue());
    }

    @Test
    @DisplayName("客制化项目有门店状态记录 - 如实返回门店状态")
    void getCustomerDetail_itemStoreStatusExists_returnsActualStatus() {
        StoreCustomizationItemStatusEntity itemStatus = StoreCustomizationItemStatusEntity.builder()
                .storeId(STORE_ID)
                .itemId(ITEM_ID_SIZE)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(
                List.of(buildBinding(ITEM_ID_SIZE, 1)));
        when(customizationItemMapper.selectList(any())).thenReturn(
                List.of(buildActiveItem(ITEM_ID_SIZE, "规格")));
        when(storeItemStatusMapper.selectList(any())).thenReturn(List.of(itemStatus));
        when(customizationOptionMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getCustomizationItems().get(0).getStoreStatus())
                .isEqualTo(StoreCustomizationStatus.ENABLED.getValue());
    }

    @Test
    @DisplayName("客制化选项无门店状态记录 - 选项门店状态默认为售罄")
    void getCustomerDetail_noOptionStoreStatus_defaultsToDisabled() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(
                List.of(buildBinding(ITEM_ID_SIZE, 1)));
        when(customizationItemMapper.selectList(any())).thenReturn(
                List.of(buildActiveItem(ITEM_ID_SIZE, "规格")));
        when(storeItemStatusMapper.selectList(any())).thenReturn(List.of());
        when(customizationOptionMapper.selectList(any())).thenReturn(
                List.of(buildActiveOption(OPTION_ID_LARGE, ITEM_ID_SIZE, "大杯")));
        when(storeOptionStatusMapper.selectList(any())).thenReturn(List.of());

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getCustomizationItems().get(0).getOptions().get(0).getStoreStatus())
                .isEqualTo(StoreCustomizationStatus.DISABLED.getValue());
    }

    @Test
    @DisplayName("客制化选项有门店状态记录 - 如实返回门店状态")
    void getCustomerDetail_optionStoreStatusExists_returnsActualStatus() {
        StoreCustomizationOptionStatusEntity optionStatus = StoreCustomizationOptionStatusEntity.builder()
                .storeId(STORE_ID)
                .optionId(OPTION_ID_LARGE)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(bindingMapper.selectList(any())).thenReturn(
                List.of(buildBinding(ITEM_ID_SIZE, 1)));
        when(customizationItemMapper.selectList(any())).thenReturn(
                List.of(buildActiveItem(ITEM_ID_SIZE, "规格")));
        when(storeItemStatusMapper.selectList(any())).thenReturn(List.of());
        when(customizationOptionMapper.selectList(any())).thenReturn(
                List.of(buildActiveOption(OPTION_ID_LARGE, ITEM_ID_SIZE, "大杯")));
        when(storeOptionStatusMapper.selectList(any())).thenReturn(List.of(optionStatus));

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getCustomizationItems().get(0).getOptions().get(0).getStoreStatus())
                .isEqualTo(StoreCustomizationStatus.ENABLED.getValue());
    }

    // ──────────────────────────────────────────────
    // 完整场景
    // ──────────────────────────────────────────────

    @Test
    @DisplayName("商品上架且有激活的客制化项目和选项 - 返回完整详情")
    void getCustomerDetail_fullScenario_returnsCompleteDetail() {
        StoreProductStatusEntity productStoreStatus = StoreProductStatusEntity.builder()
                .storeId(STORE_ID)
                .productId(PRODUCT_ID)
                .status(StoreProductStatus.ENABLED.getValue())
                .build();
        StoreCustomizationItemStatusEntity itemStoreStatus = StoreCustomizationItemStatusEntity.builder()
                .storeId(STORE_ID)
                .itemId(ITEM_ID_SIZE)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();
        StoreCustomizationOptionStatusEntity optionStoreStatus = StoreCustomizationOptionStatusEntity.builder()
                .storeId(STORE_ID)
                .optionId(OPTION_ID_LARGE)
                .status(StoreCustomizationStatus.ENABLED.getValue())
                .build();

        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildEnabledProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(productStoreStatus);
        when(bindingMapper.selectList(any())).thenReturn(
                List.of(buildBinding(ITEM_ID_SIZE, 1)));
        when(customizationItemMapper.selectList(any())).thenReturn(
                List.of(buildActiveItem(ITEM_ID_SIZE, "规格")));
        when(storeItemStatusMapper.selectList(any())).thenReturn(List.of(itemStoreStatus));
        when(customizationOptionMapper.selectList(any())).thenReturn(List.of(
                buildActiveOption(OPTION_ID_LARGE, ITEM_ID_SIZE, "大杯"),
                buildActiveOption(OPTION_ID_MEDIUM, ITEM_ID_SIZE, "中杯")));
        when(storeOptionStatusMapper.selectList(any())).thenReturn(List.of(optionStoreStatus));

        ApiResponse<CustomerProductDetailResponse> response =
                productBizService.getCustomerDetail(PRODUCT_ID, STORE_ID);

        assertThat(response.isSuccess()).isTrue();
        CustomerProductDetailResponse data = response.getData();
        assertThat(data.getId()).isEqualTo(PRODUCT_ID);
        assertThat(data.getGlobalStatus()).isEqualTo(ProductStatus.ENABLED.getValue());
        assertThat(data.getStoreStatus()).isEqualTo(StoreProductStatus.ENABLED.getValue());
        assertThat(data.getCustomizationItems()).hasSize(1);

        var item = data.getCustomizationItems().get(0);
        assertThat(item.getId()).isEqualTo(ITEM_ID_SIZE);
        assertThat(item.getStoreStatus()).isEqualTo(StoreCustomizationStatus.ENABLED.getValue());
        assertThat(item.getOptions()).hasSize(2);

        var largeOption = item.getOptions().stream()
                .filter(o -> o.getId().equals(OPTION_ID_LARGE))
                .findFirst().orElseThrow();
        assertThat(largeOption.getStoreStatus()).isEqualTo(StoreCustomizationStatus.ENABLED.getValue());

        var mediumOption = item.getOptions().stream()
                .filter(o -> o.getId().equals(OPTION_ID_MEDIUM))
                .findFirst().orElseThrow();
        assertThat(mediumOption.getStoreStatus()).isEqualTo(StoreCustomizationStatus.DISABLED.getValue());
    }
}
