package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.service.CustomizationOptionAdminService;
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
public class CustomizationOptionAdminController {

    private final CustomizationOptionAdminService customizationOptionAdminService;

    /**
     * 创建客制化选项
     * @param itemId 客制化项目ID
     * @param request 创建客制化选项请求参数
     * @return 创建成功的客制化选项信息
     */
    @PostMapping("/v1/admin/customization-items/{itemId}/options")
    public ApiResponse<CreateCustomizationOptionResponse> createOption(
            @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID不合法") Long itemId,
            @Valid @RequestBody CreateCustomizationOptionRequest request) {
        return customizationOptionAdminService.createOption(itemId, request);
    }

    /**
     * 查询客制化项目下的选项列表
     * @param itemId 客制化项目ID
     * @return 选项详情列表
     */
    @GetMapping("/v1/admin/customization-items/{itemId}/options")
    public ApiResponse<List<CustomizationOptionDetailResponse>> listOptions(
            @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID不合法") Long itemId) {
        return customizationOptionAdminService.listOptions(itemId);
    }

    /**
     * 更新客制化选项
     * @param id 客制化选项ID
     * @param request 更新客制化选项请求参数
     * @return 更新后的客制化选项详情
     */
    @PutMapping("/v1/admin/customization-options/{id}")
    public ApiResponse<CustomizationOptionDetailResponse> updateOption(
            @PathVariable("id") @Min(value = 1, message = "客制化选项ID不合法") Long id,
            @Valid @RequestBody UpdateCustomizationOptionRequest request) {
        return customizationOptionAdminService.updateOption(id, request);
    }

    /**
     * 删除客制化选项
     * @param id 客制化选项ID
     * @return 操作结果
     */
    @DeleteMapping("/v1/admin/customization-options/{id}")
    public ApiResponse<Void> deleteOption(
            @PathVariable("id") @Min(value = 1, message = "客制化选项ID不合法") Long id) {
        return customizationOptionAdminService.deleteOption(id);
    }
}
