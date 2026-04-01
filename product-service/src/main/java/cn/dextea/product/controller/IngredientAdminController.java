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

    @PostMapping
    public ApiResponse<CreateIngredientResponse> createIngredient(
            @Valid @RequestBody CreateIngredientRequest request) {
        return ingredientAdminService.createIngredient(request);
    }

    @GetMapping
    public ApiResponse<IPage<IngredientDetailResponse>> getIngredientPage(
            @Valid IngredientPageQueryRequest request) {
        return ingredientAdminService.getIngredientPage(request);
    }

    @GetMapping("/{id}")
    public ApiResponse<IngredientDetailResponse> getIngredientDetail(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return ingredientAdminService.getIngredientDetail(id);
    }

    @PutMapping("/{id}")
    public ApiResponse<IngredientDetailResponse> updateIngredient(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id,
            @Valid @RequestBody UpdateIngredientRequest request) {
        return ingredientAdminService.updateIngredient(id, request);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteIngredient(
            @PathVariable("id") @Min(value = 1, message = "ID不能为空") Long id) {
        return ingredientAdminService.deleteIngredient(id);
    }
}
