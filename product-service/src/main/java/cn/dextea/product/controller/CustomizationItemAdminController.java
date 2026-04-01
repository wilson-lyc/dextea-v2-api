package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.service.CustomizationItemAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class CustomizationItemAdminController {

    private final CustomizationItemAdminService customizationItemAdminService;

    @PostMapping("/v1/admin/products/{productId}/customization-items")
    public ApiResponse<CreateCustomizationItemResponse> createItem(
            @PathVariable("productId") @Min(value = 1, message = "产品ID不合法") Long productId,
            @Valid @RequestBody CreateCustomizationItemRequest request) {
        return customizationItemAdminService.createItem(productId, request);
    }

    @GetMapping("/v1/admin/products/{productId}/customization-items")
    public ApiResponse<List<CustomizationItemDetailResponse>> getProductCustomizations(
            @PathVariable("productId") @Min(value = 1, message = "产品ID不合法") Long productId) {
        return customizationItemAdminService.getProductCustomizations(productId);
    }

    @PutMapping("/v1/admin/customization-items/{itemId}")
    public ApiResponse<CustomizationItemDetailResponse> updateItem(
            @PathVariable("itemId") @Min(value = 1, message = "定制项ID不合法") Long itemId,
            @Valid @RequestBody UpdateCustomizationItemRequest request) {
        return customizationItemAdminService.updateItem(itemId, request);
    }

    @DeleteMapping("/v1/admin/customization-items/{itemId}")
    public ApiResponse<Void> deleteItem(
            @PathVariable("itemId") @Min(value = 1, message = "定制项ID不合法") Long itemId) {
        return customizationItemAdminService.deleteItem(itemId);
    }
}
