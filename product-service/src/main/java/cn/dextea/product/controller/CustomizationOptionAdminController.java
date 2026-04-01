package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BindOptionIngredientRequest;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.dto.response.OptionIngredientResponse;
import cn.dextea.product.service.CustomizationOptionAdminService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class CustomizationOptionAdminController {

    private final CustomizationOptionAdminService customizationOptionAdminService;

    @PostMapping("/v1/admin/customization-items/{itemId}/options")
    public ApiResponse<CreateCustomizationOptionResponse> createOption(
            @PathVariable("itemId") @Min(value = 1, message = "定制项ID不合法") Long itemId,
            @Valid @RequestBody CreateCustomizationOptionRequest request) {
        return customizationOptionAdminService.createOption(itemId, request);
    }

    @PutMapping("/v1/admin/customization-options/{optionId}")
    public ApiResponse<CustomizationOptionDetailResponse> updateOption(
            @PathVariable("optionId") @Min(value = 1, message = "定制选项ID不合法") Long optionId,
            @Valid @RequestBody UpdateCustomizationOptionRequest request) {
        return customizationOptionAdminService.updateOption(optionId, request);
    }

    @DeleteMapping("/v1/admin/customization-options/{optionId}")
    public ApiResponse<Void> deleteOption(
            @PathVariable("optionId") @Min(value = 1, message = "定制选项ID不合法") Long optionId) {
        return customizationOptionAdminService.deleteOption(optionId);
    }

    @PutMapping("/v1/admin/customization-options/{optionId}/ingredient")
    public ApiResponse<OptionIngredientResponse> bindIngredient(
            @PathVariable("optionId") @Min(value = 1, message = "定制选项ID不合法") Long optionId,
            @Valid @RequestBody BindOptionIngredientRequest request) {
        return customizationOptionAdminService.bindIngredient(optionId, request);
    }

    @DeleteMapping("/v1/admin/customization-options/{optionId}/ingredient")
    public ApiResponse<Void> unbindIngredient(
            @PathVariable("optionId") @Min(value = 1, message = "定制选项ID不合法") Long optionId) {
        return customizationOptionAdminService.unbindIngredient(optionId);
    }
}
