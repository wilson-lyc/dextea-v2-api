package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.entity.StoreProductStatusEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.StoreProductStatus;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.mapper.StoreProductStatusMapper;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProductBizService - 商品门店状态单元测试")
class ProductBizServiceImplTest {

    private static final Long PRODUCT_ID = 1L;
    private static final Long STORE_ID = 10L;
    private static final Long NON_EXISTENT_ID = 999L;

    @BeforeAll
    static void initMybatisPlusLambdaCache() {
        MybatisPlusTestSupport.initTableInfo(StoreProductStatusEntity.class);
    }

    @Mock
    private ProductMapper productMapper;

    @Mock
    private StoreProductStatusMapper storeProductStatusMapper;

    @Spy
    private ProductConverter productConverter = new ProductConverter();

    @InjectMocks
    private ProductBizServiceImpl productBizService;

    private ProductEntity buildProduct() {
        return ProductEntity.builder()
                .id(PRODUCT_ID)
                .name("杨枝甘露")
                .price(new BigDecimal("28.00"))
                .build();
    }

    @Test
    @DisplayName("首次为门店设置商品状态 - 插入新记录，设为在售")
    void updateStatus_firstTime_insertsNewStatusRecord() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(storeProductStatusMapper.insert(any(StoreProductStatusEntity.class))).thenReturn(1);

        UpdateStoreProductStatusRequest request = UpdateStoreProductStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreProductStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response = productBizService.updateStatus(PRODUCT_ID, request);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("门店已有商品状态记录 - 更新为售罄")
    void updateStatus_existingRecord_updatesStatusToDisabled() {
        StoreProductStatusEntity existing = StoreProductStatusEntity.builder()
                .storeId(STORE_ID)
                .productId(PRODUCT_ID)
                .status(StoreProductStatus.ENABLED.getValue())
                .build();

        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(existing);
        when(storeProductStatusMapper.update(any(), any())).thenReturn(1);

        UpdateStoreProductStatusRequest request = UpdateStoreProductStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreProductStatus.DISABLED.getValue())
                .build();

        ApiResponse<Void> response = productBizService.updateStatus(PRODUCT_ID, request);

        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("设置门店商品状态 - 商品不存在，返回 PRODUCT_NOT_FOUND 错误")
    void updateStatus_productNotFound_returnsProductNotFoundError() {
        when(productMapper.selectById(NON_EXISTENT_ID)).thenReturn(null);

        UpdateStoreProductStatusRequest request = UpdateStoreProductStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreProductStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response = productBizService.updateStatus(NON_EXISTENT_ID, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ProductErrorCode.PRODUCT_NOT_FOUND.getCode());
    }

    @Test
    @DisplayName("首次设置门店商品状态 - 数据库插入失败，返回 STORE_SALE_STATUS_UPDATE_FAILED 错误")
    void updateStatus_insertFails_returnsStoreStatusUpdateFailedError() {
        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(null);
        when(storeProductStatusMapper.insert(any(StoreProductStatusEntity.class))).thenReturn(0);

        UpdateStoreProductStatusRequest request = UpdateStoreProductStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreProductStatus.ENABLED.getValue())
                .build();

        ApiResponse<Void> response = productBizService.updateStatus(PRODUCT_ID, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ProductErrorCode.STORE_SALE_STATUS_UPDATE_FAILED.getCode());
    }

    @Test
    @DisplayName("更新已有门店商品状态 - 数据库更新失败，返回 STORE_SALE_STATUS_UPDATE_FAILED 错误")
    void updateStatus_updateFails_returnsStoreStatusUpdateFailedError() {
        StoreProductStatusEntity existing = StoreProductStatusEntity.builder()
                .storeId(STORE_ID)
                .productId(PRODUCT_ID)
                .status(StoreProductStatus.ENABLED.getValue())
                .build();

        when(productMapper.selectById(PRODUCT_ID)).thenReturn(buildProduct());
        when(storeProductStatusMapper.selectOne(any())).thenReturn(existing);
        when(storeProductStatusMapper.update(any(), any())).thenReturn(0);

        UpdateStoreProductStatusRequest request = UpdateStoreProductStatusRequest.builder()
                .storeId(STORE_ID)
                .status(StoreProductStatus.DISABLED.getValue())
                .build();

        ApiResponse<Void> response = productBizService.updateStatus(PRODUCT_ID, request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ProductErrorCode.STORE_SALE_STATUS_UPDATE_FAILED.getCode());
    }
}
