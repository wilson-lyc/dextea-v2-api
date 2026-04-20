package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.response.StoreProductCustomizationItemDetailResponse;
import cn.dextea.product.service.ProductCustomizationItemBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "商品客制化项目关联（Biz）", description = "门店端查询商品客制化项目列表接口")
@RestController
@RequestMapping("/v1/biz/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductCustomizationItemBizController {

    private final ProductCustomizationItemBizService productCustomizationItemBizService;

    /**
     * 获取商品客制化项目列表（门店端）
     * @param productId 商品ID
     * @param storeId 门店ID
     * @return 商品客制化项目列表（含门店状态）
     */
    @Operation(summary = "获取商品客制化项目列表（门店端）", description = "返回商品绑定的客制化项目列表，含门店在售状态")
    @GetMapping("/{productId}/customization-items")
    public ApiResponse<List<StoreProductCustomizationItemDetailResponse>> getProductCustomizationItems(
            @Parameter(description = "商品ID") @PathVariable("productId") @Min(value = 1, message = "商品ID不能为空") Long productId,
            @Parameter(description = "门店ID") @RequestParam("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId) {
        return productCustomizationItemBizService.getProductCustomizationItems(productId, storeId);
    }
}
