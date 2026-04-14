package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.CustomizationItemPageRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemStatusRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.service.CustomizationItemAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "客制化项目管理接口", description = "适用于公司端")
@RestController
@RequestMapping("/v1/admin/customization-items")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class CustomizationItemAdminController {

    private final CustomizationItemAdminService customizationItemAdminService;

    /**
     * 创建客制化项目
     * @param request 创建客制化项目请求参数
     * @return 创建成功的客制化项目信息
     */
    @Operation(summary = "创建客制化项目")
    @PostMapping
    public ApiResponse<CreateCustomizationItemResponse> create(
            @Valid @RequestBody CreateCustomizationItemRequest request) {
        return customizationItemAdminService.create(request);
    }

    /**
     * 分页查询客制化项目列表（公司端）
     * @param request 分页查询请求参数
     * @return 客制化项目分页数据
     */
    @Operation(summary = "分页查询客制化项目列表（公司端）")
    @GetMapping
    public ApiResponse<IPage<CustomizationItemDetailResponse>> getPage(
            @Valid CustomizationItemPageRequest request) {
        return customizationItemAdminService.getPage(request);
    }

    /**
     * 获取客制化项目详情
     * @param id 客制化项目ID
     * @return 客制化项目详情
     */
    @Operation(summary = "获取客制化项目详情", description = "返回客制化项目详情")
    @GetMapping("/{id}")
    public ApiResponse<CustomizationItemDetailResponse> getDetail(
            @Parameter(description = "客制化项目ID") @PathVariable("id") @Min(value = 1, message = "客制化项目ID不合法") Long id) {
        return customizationItemAdminService.getDetail(id);
    }

    /**
     * 更新客制化项目信息
     * @param id 客制化项目ID
     * @param request 更新客制化项目请求参数
     * @return 更新后的客制化项目详情
     */
    @Operation(summary = "更新客制化项目信息")
    @PutMapping("/{id}/info")
    public ApiResponse<CustomizationItemDetailResponse> updateInfo(
            @Parameter(description = "客制化项目ID") @PathVariable("id") @Min(value = 1, message = "客制化项目ID不合法") Long id,
            @Valid @RequestBody UpdateCustomizationItemRequest request) {
        return customizationItemAdminService.updateInfo(id, request);
    }

    /**
     * 更新客制化项目全局状态
     * @param id 客制化项目ID
     * @param request 客制化项目状态更新请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新客制化项目全局状态")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @Parameter(description = "客制化项目ID") @PathVariable("id") @Min(value = 1, message = "客制化项目ID不合法") Long id,
            @Valid @RequestBody UpdateCustomizationItemStatusRequest request) {
        return customizationItemAdminService.updateStatus(id, request);
    }

}
