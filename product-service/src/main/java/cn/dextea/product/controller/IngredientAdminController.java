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
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/{id}")
    public ApiResponse<IngredientDetailResponse> getIngredientDetail(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return ingredientAdminService.getIngredientDetail(id);
    }

    /**
     * 更新原料信息
     * @param id 原料ID
     * @param request 更新原料请求参数
     * @return 更新后的原料详情
     */
    @PutMapping("/{id}")
    public ApiResponse<IngredientDetailResponse> updateIngredient(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateIngredientRequest request) {
        return ingredientAdminService.updateIngredient(id, request);
    }

    /**
     * 删除原料（禁用）
     * @param id 原料ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteIngredient(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return ingredientAdminService.deleteIngredient(id);
    }
}