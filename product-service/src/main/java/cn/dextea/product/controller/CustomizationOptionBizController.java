package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.ItemOptionsListInStore;
import cn.dextea.product.dto.request.UpdateOptionStoreStatusRequest;
import cn.dextea.product.dto.response.OptionDetailResponse;
import cn.dextea.product.service.CustomizationOptionBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "客制化选项（Biz）", description = "门店端客制化选项查询与在售状态管理接口")
@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class CustomizationOptionBizController {

    private final CustomizationOptionBizService customizationOptionBizService;

    /**
     * 查询客制化项目下的选项列表（门店端）
     * @param itemId 客制化项目ID
     * @param request 查询请求参数（含门店ID）
     * @return 选项列表（含门店状态）
     */
    @Operation(summary = "查询客制化项目下的选项列表（门店端）", description = "适用于门店端获取客制化选项列表")
    @GetMapping("/v1/biz/customization-items/{itemId}/options")
    public ApiResponse<List<OptionDetailResponse>> getItemOptionsList(
            @Parameter(description = "客制化项目ID") @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID错误") Long itemId,
            @Valid ItemOptionsListInStore request) {
        return customizationOptionBizService.getItemOptionsList(itemId, request);
    }

    /**
     * 门店端更新客制化选项门店状态
     * @param id 客制化选项ID
     * @param request 门店ID与门店状态
     * @return 操作结果
     */
    @Operation(summary = "更新客制化选项的门店状态")
    @PutMapping("/v1/biz/customization-options/{id}/status")
    public ApiResponse<Void> updateOptionStoreStatus(
            @Parameter(description = "客制化选项ID") @PathVariable("id") @Min(value = 1, message = "客制化选项ID不合法") Long id,
            @Valid @RequestBody UpdateOptionStoreStatusRequest request) {
        return customizationOptionBizService.updateOptionStoreStatus(id, request);
    }
}
