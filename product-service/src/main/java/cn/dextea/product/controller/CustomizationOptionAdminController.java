package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateOptionRequest;
import cn.dextea.product.dto.request.UpdateOptionStatusRequest;
import cn.dextea.product.dto.request.UpdateOptionInfoRequest;
import cn.dextea.product.dto.response.CreateCustomizationOptionResponse;
import cn.dextea.product.dto.response.OptionDetailResponse;
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

@Tag(name = "客制化选项管理接口", description = "适用于公司端")
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
            @Parameter(description = "客制化项目ID") @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID错误") Long itemId,
            @Valid @RequestBody CreateOptionRequest request) {
        return customizationOptionAdminService.createOption(itemId, request);
    }

    /**
     * 查询客制化项目下的选项列表
     * @param itemId 客制化项目ID
     * @return 选项详情列表
     */
    @Operation(summary = "查询客制化项目下的选项列表")
    @GetMapping("/v1/admin/customization-items/{itemId}/options")
    public ApiResponse<List<OptionDetailResponse>> getItemOptionsList(
            @Parameter(description = "客制化项目ID") @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID错误") Long itemId) {
        return customizationOptionAdminService.getItemOptionsList(itemId);
    }

    /**
     * 查询客制化选项详情
     * @param id 客制化选项ID
     * @return 客制化选项详情
     */
    @Operation(summary = "查询客制化选项详情")
    @GetMapping("/v1/admin/customization-options/{id}")
    public ApiResponse<OptionDetailResponse> getOptionDetail(
            @Parameter(description = "客制化选项ID") @PathVariable("id") @Min(value = 1, message = "客制化选项ID错误") Long id) {
        return customizationOptionAdminService.getOptionDetail(id);
    }

    /**
     * 更新客制化选项信息
     * @param id 客制化选项ID
     * @param request 更新客制化选项信息请求参数
     * @return 更新后的客制化选项详情
     */
    @Operation(summary = "更新客制化选项信息")
    @PutMapping("/v1/admin/customization-options/{id}/info")
    public ApiResponse<OptionDetailResponse> updateOptionInfo(
            @Parameter(description = "客制化选项ID") @PathVariable("id") @Min(value = 1, message = "客制化选项ID错误") Long id,
            @Valid @RequestBody UpdateOptionInfoRequest request) {
        return customizationOptionAdminService.updateOptionInfo(id, request);
    }

    /**
     * 更新客制化选项全局状态
     * @param id 客制化选项ID
     * @param request 选项新全局状态
     * @return 操作结果
     */
    @Operation(summary = "更新客制化选项全局状态")
    @PutMapping("/v1/admin/customization-options/{id}/status")
    public ApiResponse<Void> updateOptionStatus(
            @Parameter(description = "客制化选项ID") @PathVariable("id") @Min(value = 1, message = "客制化选项ID错误") Long id,
            @Valid @RequestBody UpdateOptionStatusRequest request) {
        return customizationOptionAdminService.updateOptionStatus(id, request);
    }
}
