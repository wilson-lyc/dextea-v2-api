package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateProductRequest;
import cn.dextea.product.dto.request.ProductPageQueryRequest;
import cn.dextea.product.dto.request.UpdateProductRequest;
import cn.dextea.product.dto.response.CreateProductResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.service.ProductAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/admin/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductAdminController {

    private final ProductAdminService productAdminService;

    @PostMapping
    public ApiResponse<CreateProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest request) {
        return productAdminService.createProduct(request);
    }

    @GetMapping
    public ApiResponse<IPage<ProductDetailResponse>> getProductPage(
            @Valid ProductPageQueryRequest request) {
        return productAdminService.getProductPage(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDetailResponse> getProductDetail(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return productAdminService.getProductDetail(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductDetailResponse> updateProduct(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        return productAdminService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProduct(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return productAdminService.deleteProduct(id);
    }
}
