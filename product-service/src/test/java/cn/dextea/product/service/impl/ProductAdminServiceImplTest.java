package cn.dextea.product.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.converter.ProductConverter;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.entity.ProductEntity;
import cn.dextea.product.enums.ProductErrorCode;
import cn.dextea.product.enums.ProductStatus;
import cn.dextea.product.mapper.ProductMapper;
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
@DisplayName("ProductAdminService 单元测试")
class ProductAdminServiceImplTest {

    @Mock
    private ProductMapper productMapper;

    @Spy
    private ProductConverter productConverter = new ProductConverter();

    @InjectMocks
    private ProductAdminServiceImpl productAdminService;

    @Test
    @DisplayName("创建商品杨枝甘露 - 成功")
    void create_yangZhiGanLu_success() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("杨枝甘露")
                .description("经典港式甜品饮品，芒果、西柚与椰奶的完美结合")
                .price(new BigDecimal("28.00"))
                .build();

        when(productMapper.exists(any())).thenReturn(false);
        when(productMapper.insert(any(ProductEntity.class))).thenAnswer(invocation -> {
            ProductEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return 1;
        });

        ApiResponse<CreateProductResponse> response = productAdminService.create(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getId()).isEqualTo(1L);
        assertThat(response.getData().getName()).isEqualTo("杨枝甘露");
        assertThat(response.getData().getPrice()).isEqualByComparingTo(new BigDecimal("28.00"));
        assertThat(response.getData().getStatus()).isEqualTo(ProductStatus.DISABLED.getValue());
    }

    @Test
    @DisplayName("创建商品 - 名称首尾空格会被去除")
    void create_nameWithWhitespace_isTrimmed() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("  杨枝甘露  ")
                .price(new BigDecimal("28.00"))
                .build();

        when(productMapper.exists(any())).thenReturn(false);
        when(productMapper.insert(any(ProductEntity.class))).thenAnswer(invocation -> {
            ProductEntity entity = invocation.getArgument(0);
            entity.setId(1L);
            return 1;
        });

        ApiResponse<CreateProductResponse> response = productAdminService.create(request);

        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData().getName()).isEqualTo("杨枝甘露");
    }

    @Test
    @DisplayName("创建商品 - 名称已存在，返回 NAME_ALREADY_EXISTS 错误")
    void create_duplicateName_returnsNameAlreadyExistsError() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("杨枝甘露")
                .price(new BigDecimal("28.00"))
                .build();

        when(productMapper.exists(any())).thenReturn(true);

        ApiResponse<CreateProductResponse> response = productAdminService.create(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ProductErrorCode.NAME_ALREADY_EXISTS.getCode());
    }

    @Test
    @DisplayName("创建商品 - 数据库写入失败，返回 CREATE_FAILED 错误")
    void create_mapperInsertFails_returnsCreateFailedError() {
        CreateProductRequest request = CreateProductRequest.builder()
                .name("杨枝甘露")
                .price(new BigDecimal("28.00"))
                .build();

        when(productMapper.exists(any())).thenReturn(false);
        when(productMapper.insert(any(ProductEntity.class))).thenReturn(0);

        ApiResponse<CreateProductResponse> response = productAdminService.create(request);

        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getCode()).isEqualTo(ProductErrorCode.CREATE_FAILED.getCode());
    }
}
