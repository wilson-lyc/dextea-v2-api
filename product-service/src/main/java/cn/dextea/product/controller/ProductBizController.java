package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.StoreProductPageRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.service.ProductBizService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(name = "商品业务接口", description = "适用于门店端管理商品和顾客端获取商品数据")
@RestController
@RequestMapping("/v1/biz/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductBizController {

    private final ProductBizService productBizService;

    /**
     * 分页查询商品列表（门店端）
     * @param request 门店ID、商品名、门店在售状态（status: 0=售罄，1=在售）、分页参数
     * @return 商品分页列表，status 为该商品在门店内的在售状态（0售罄，1在售）
     */
    @Operation(summary = "分页查询商品列表（门店端）", description = "status 字段为该商品在当前门店的在售状态：0=售罄，1=在售。可传 saleStatus 按门店在售状态筛选，不传则返回全部")
    @GetMapping
    public ApiResponse<IPage<ProductDetailResponse>> getProductPage(
            @Valid StoreProductPageRequest request) {
        return productBizService.getProductPage(request);
    }

    /**
     * 更新商品门店状态
     * @param id 商品ID
     * @param request 门店ID与在售状态
     * @return 操作结果
     */
    @Operation(summary = "更新商品门店状态", description = "适用于门店端更新商品的门店状态")
    @PutMapping("/{id}/status")
    public ApiResponse<Void> updateStatus(
            @Parameter(description = "商品ID") @PathVariable("id") @Min(value = 1, message = "商品ID不能为空") Long id,
            @Valid @RequestBody UpdateStoreProductStatusRequest request) {
        return productBizService.updateStatus(id, request);
    }

    /**
     * 查询商品详情（顾客端）
     * @param id 商品ID
     * @param storeId 门店ID
     * @return 商品详情及客制化数据，商品全局下架时返回错误
     */
    @Operation(summary = "查询商品详情（顾客端）", description = "返回商品详情及客制化项目/选项，商品全局下架时返回业务错误")
    @GetMapping("/{id}")
    public ApiResponse<ProductBizDetailResponse> getProductDetail(
            @Parameter(description = "商品ID") @PathVariable("id") @Min(value = 1, message = "商品ID不能为空") Long id,
            @Parameter(description = "门店ID") @RequestParam("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId) {
        return productBizService.getProductDetail(id, storeId);
    }
}
