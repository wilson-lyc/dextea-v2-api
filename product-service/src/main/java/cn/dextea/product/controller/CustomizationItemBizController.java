package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.StorePageQueryCustomizationItemRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationItemStatusRequest;
import cn.dextea.product.dto.response.CustomizationItemDetailResponse;
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

@Tag(name = "客制化项目业务接口", description = "适用于门店端")
@RestController
@RequestMapping("/v1/biz/customization-items")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class CustomizationItemBizController {

    private final CustomizationItemBizService customizationItemBizService;

    /**
     * 分页查询客制化项目列表（门店端）
     * @param request 分页、项目名、全局状态、门店状态
     * @return 客制化项目分页数据
     */
    @Operation(summary = "分页查询客制化项目列表（门店端）", description = "支持项目名模糊查询，支持按全局状态和门店状态分别筛选")
    @GetMapping
    public ApiResponse<IPage<CustomizationItemDetailResponse>> getPage(
            @Valid StorePageQueryCustomizationItemRequest request) {
        return customizationItemBizService.getPage(request);
    }

    /**
     * 门店端更新客制化项目门店状态
     * @param id 客制化项目ID
     * @param request 门店ID与门店状态
     * @return 操作结果
     */
    @Operation(summary = "更新客制化项目的门店状态")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @Parameter(description = "客制化项目ID") @PathVariable("id") @Min(value = 1, message = "客制化项目ID不合法") Long id,
            @Valid @RequestBody UpdateStoreCustomizationItemStatusRequest request) {
        return customizationItemBizService.updateStatus(id, request);
    }
}
