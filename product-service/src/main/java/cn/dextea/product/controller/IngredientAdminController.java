package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CreateIngredientRequest;
import cn.dextea.product.dto.request.IngredientPageQueryRequest;
import cn.dextea.product.dto.request.UpdateIngredientRequest;
import cn.dextea.product.dto.response.CreateIngredientResponse;
import cn.dextea.product.dto.response.IngredientDetailResponse;
import cn.dextea.product.service.IngredientAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "原料管理（Admin）", description = "管理端原料 CRUD 接口")
@RestController
@RequestMapping("/v1/admin/ingredients")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class IngredientAdminController {

    private final IngredientAdminService ingredientAdminService;

    /**
     * 创建原料
     * @param request 创建原料请求参数
     * @return 创建成功的原料信息
     */
    @Operation(summary = "创建原料")
    @PostMapping
    public ApiResponse<CreateIngredientResponse> createIngredient(
            @Valid @RequestBody CreateIngredientRequest request) {
        return ingredientAdminService.createIngredient(request);
    }

    /**
     * 分页查询原料列表
     * @param request 查询请求参数（支持按名称模糊搜索）
     * @return 原料分页列表
     */
    @Operation(summary = "分页查询原料列表", description = "支持按原料名称模糊搜索")
    @GetMapping
    public ApiResponse<IPage<IngredientDetailResponse>> getIngredientPage(
            @Valid IngredientPageQueryRequest request) {
        return ingredientAdminService.getIngredientPage(request);
    }

    /**
     * 获取原料详情
     * @param id 原料ID
     * @return 原料详情信息
     */
    @Operation(summary = "获取原料详情")
    @GetMapping("/{id}")
    public ApiResponse<IngredientDetailResponse> getIngredientDetail(
            @Parameter(description = "原料ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return ingredientAdminService.getIngredientDetail(id);
    }

    /**
     * 更新原料信息
     * @param id 原料ID
     * @param request 更新原料请求参数
     * @return 更新后的原料详情
     */
    @Operation(summary = "更新原料信息")
    @PutMapping("/{id}")
    public ApiResponse<IngredientDetailResponse> updateIngredient(
            @Parameter(description = "原料ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateIngredientRequest request) {
        return ingredientAdminService.updateIngredient(id, request);
    }

    /**
     * 删除原料（禁用）
     * @param id 原料ID
     * @return 操作结果
     */
    @Operation(summary = "删除原料（禁用）")
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteIngredient(
            @Parameter(description = "原料ID") @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return ingredientAdminService.deleteIngredient(id);
    }
}