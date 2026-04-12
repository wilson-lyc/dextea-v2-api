package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationOptionRequest;
import cn.dextea.product.dto.request.UpdateCustomizationOptionRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.CustomizationOptionDetailResponse;
import cn.dextea.product.service.CustomizationOptionAdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客制化选项管理（Admin）", description = "管理端客制化选项 CRUD 接口，如「去冰」「全糖」等")
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
    @Operation(summary = "创建客制化选项")
    @PostMapping("/v1/admin/customization-items/{itemId}/options")
    public ApiResponse<CreateCustomizationOptionResponse> createOption(
            @Parameter(description = "客制化项目ID") @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID不合法") Long itemId,
            @Valid @RequestBody CreateCustomizationOptionRequest request) {
        return customizationOptionAdminService.createOption(itemId, request);
    }

    /**
     * 查询客制化项目下的选项列表
     * @param itemId 客制化项目ID
     * @return 选项详情列表
     */
    @Operation(summary = "查询客制化项目下的选项列表")
    @GetMapping("/v1/admin/customization-items/{itemId}/options")
    public ApiResponse<List<CustomizationOptionDetailResponse>> listOptions(
            @Parameter(description = "客制化项目ID") @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID不合法") Long itemId) {
        return customizationOptionAdminService.listOptions(itemId);
    }

    /**
     * 更新客制化选项
     * @param id 客制化选项ID
     * @param request 更新客制化选项请求参数
     * @return 更新后的客制化选项详情
     */
    @Operation(summary = "更新客制化选项")
    @PutMapping("/v1/admin/customization-options/{id}")
    public ApiResponse<CustomizationOptionDetailResponse> updateOption(
            @Parameter(description = "客制化选项ID") @PathVariable("id") @Min(value = 1, message = "客制化选项ID不合法") Long id,
            @Valid @RequestBody UpdateCustomizationOptionRequest request) {
        return customizationOptionAdminService.updateOption(id, request);
    }

    /**
     * 删除客制化选项
     * @param id 客制化选项ID
     * @return 操作结果
     */
    @Operation(summary = "删除客制化选项")
    @DeleteMapping("/v1/admin/customization-options/{id}")
    public ApiResponse<Void> deleteOption(
            @Parameter(description = "客制化选项ID") @PathVariable("id") @Min(value = 1, message = "客制化选项ID不合法") Long id) {
        return customizationOptionAdminService.deleteOption(id);
    }
}
