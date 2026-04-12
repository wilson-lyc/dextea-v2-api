package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CustomizationItemPageQueryWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationItemSaleRequest;
import cn.dextea.product.dto.response.CustomizationItemWithStoreStatusResponse;
import cn.dextea.product.service.CustomizationItemBizService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "客制化项目（Biz）", description = "门店端客制化项目查询与在售状态管理接口")
@RestController
@RequestMapping("/v1/biz/customization-items")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class CustomizationItemBizController {

    private final CustomizationItemBizService customizationItemBizService;

    /**
     * 门店端分页查询客制化项目列表（含门店在售状态，仅返回全局激活项目）
     * @param request 分页查询请求参数（含门店ID）
     * @return 客制化项目分页数据（含门店状态）
     */
    @Operation(summary = "分页查询客制化项目列表（门店端）", description = "仅返回全局激活的客制化项目，并附带该项目在当前门店的在售状态")
    @GetMapping
    public ApiResponse<IPage<CustomizationItemWithStoreStatusResponse>> page(
            @Valid CustomizationItemPageQueryWithStoreIdRequest request) {
        return customizationItemBizService.page(request);
    }

    /**
     * 门店端更新客制化项目在售状态
     * @param id 客制化项目ID
     * @param request 更新在售状态请求参数（含门店ID）
     * @return 操作结果
     */
    @Operation(summary = "更新客制化项目的门店在售状态")
    @PutMapping("/{id}/sale-status")
    public ApiResponse<Void> updateSaleStatus(
            @Parameter(description = "客制化项目ID") @PathVariable("id") @Min(value = 1, message = "客制化项目ID不合法") Long id,
            @Valid @RequestBody UpdateStoreCustomizationItemSaleRequest request) {
        return customizationItemBizService.updateSaleStatus(id, request);
    }
}
