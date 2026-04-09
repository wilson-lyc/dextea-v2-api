package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CustomizationOptionListWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreCustomizationOptionSaleRequest;
import cn.dextea.product.dto.response.CustomizationOptionWithStoreStatusResponse;
import cn.dextea.product.service.CustomizationOptionBizService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class CustomizationOptionBizController {

    private final CustomizationOptionBizService customizationOptionBizService;

    /**
     * 门店端查询客制化项目下的选项列表（含门店在售状态，仅返回全局激活选项）
     * @param itemId 客制化项目ID
     * @param request 查询请求参数（含门店ID）
     * @return 选项列表（含门店状态）
     */
    @GetMapping("/v1/biz/customization-items/{itemId}/options")
    public ApiResponse<List<CustomizationOptionWithStoreStatusResponse>> listOptions(
            @PathVariable("itemId") @Min(value = 1, message = "客制化项目ID不合法") Long itemId,
            @Valid CustomizationOptionListWithStoreIdRequest request) {
        return customizationOptionBizService.listOptions(itemId, request);
    }

    /**
     * 门店端更新客制化选项在售状态
     * @param id 客制化选项ID
     * @param request 更新在售状态请求参数（含门店ID）
     * @return 操作结果
     */
    @PutMapping("/v1/biz/customization-options/{id}/sale-status")
    public ApiResponse<Void> updateSaleStatus(
            @PathVariable("id") @Min(value = 1, message = "客制化选项ID不合法") Long id,
            @Valid @RequestBody UpdateStoreCustomizationOptionSaleRequest request) {
        return customizationOptionBizService.updateSaleStatus(id, request);
    }
}
