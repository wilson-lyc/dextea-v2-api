package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.IngredientPageQueryRequest;
import cn.dextea.product.dto.request.UpdateStoreIngredientInventoryRequest;
import cn.dextea.product.dto.response.StoreIngredientInventoryResponse;
import cn.dextea.product.service.IngredientBizService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "原料库存（Biz）", description = "门店端原料库存查询与更新接口")
@RestController
@RequestMapping("/v1/biz/ingredients")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class IngredientBizController {

    private final IngredientBizService ingredientBizService;

    /**
     * 查询门店原料库存列表
     * @param storeId 门店ID
     * @param request 分页查询请求参数（支持按名称模糊搜索）
     * @return 原料库存分页列表
     */
    @Operation(summary = "查询门店原料库存列表", description = "支持按原料名称模糊搜索，返回当前门店各原料的库存信息")
    @GetMapping
    public ApiResponse<IPage<StoreIngredientInventoryResponse>> getInventoryPage(
            @Parameter(description = "门店ID") @RequestParam("storeId") @Min(value = 1, message = "门店ID无效") Long storeId,
            @Valid IngredientPageQueryRequest request) {
        return ingredientBizService.getInventoryPage(storeId, request);
    }

    /**
     * 更新门店原料库存
     * @param id 原料ID
     * @param request 更新库存请求参数
     * @return 操作结果
     */
    @Operation(summary = "更新门店原料库存")
    @PutMapping("/{id}/inventory")
    public ApiResponse<Void> updateInventory(
            @Parameter(description = "原料ID") @PathVariable("id") @Min(value = 1, message = "原料ID不能为空") Long id,
            @Valid @RequestBody UpdateStoreIngredientInventoryRequest request) {
        return ingredientBizService.updateInventory(id, request);
    }
}
