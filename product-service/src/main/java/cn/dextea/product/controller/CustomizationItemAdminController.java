package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateCustomizationItemRequest;
import cn.dextea.product.dto.request.CustomizationItemPageQueryRequest;
import cn.dextea.product.dto.request.UpdateCustomizationItemRequest;
import cn.dextea.product.dto.response.CreateCustomizationItemResponse;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
import cn.dextea.product.service.CustomizationItemAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping
    public ApiResponse<CreateCustomizationItemResponse> create(
            @Valid @RequestBody CreateCustomizationItemRequest request) {
        return customizationItemAdminService.create(request);
    }

    /**
     * 分页查询客制化项目列表
     * @param request 分页查询请求参数
     * @return 客制化项目分页数据
     */
    @GetMapping
    public ApiResponse<IPage<CustomizationItemDetailResponse>> page(
            @Valid CustomizationItemPageQueryRequest request) {
        return customizationItemAdminService.page(request);
    }

    /**
     * 查询客制化项目详情
     * @param id 客制化项目ID
     * @return 客制化项目详情（含选项列表）
     */
    @GetMapping("/{id}")
    public ApiResponse<CustomizationItemDetailResponse> detail(
            @PathVariable("id") @Min(value = 1, message = "客制化项目ID不合法") Long id) {
        return customizationItemAdminService.detail(id);
    }

    /**
     * 更新客制化项目
     * @param id 客制化项目ID
     * @param request 更新客制化项目请求参数
     * @return 更新后的客制化项目详情
     */
    @PutMapping("/{id}")
    public ApiResponse<CustomizationItemDetailResponse> update(
            @PathVariable("id") @Min(value = 1, message = "客制化项目ID不合法") Long id,
            @Valid @RequestBody UpdateCustomizationItemRequest request) {
        return customizationItemAdminService.update(id, request);
    }

    /**
     * 删除客制化项目
     * @param id 客制化项目ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(
            @PathVariable("id") @Min(value = 1, message = "客制化项目ID不合法") Long id) {
        return customizationItemAdminService.delete(id);
    }
}
