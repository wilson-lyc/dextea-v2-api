package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.ProductPageQueryWithStoreIdRequest;
import cn.dextea.product.dto.request.UpdateStoreProductSaleRequest;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
import cn.dextea.product.dto.response.ProductDetailResponse;
import cn.dextea.product.service.ProductBizService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/biz/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductBizController {

    private final ProductBizService productBizService;

    /**
     * 分页查询商品列表（门店端）
     * @param request 门店ID、商品名、分页参数
     * @return 商品分页列表，status 为该商品在门店内的在售状态（0售罄，1在售）
     */
    @GetMapping
    public ApiResponse<IPage<ProductDetailResponse>> getProductPage(
            @Valid ProductPageQueryWithStoreIdRequest request) {
        return productBizService.getProductPage(request);
    }

    /**
     * 查询商品详情（顾客端）
     * @param id 商品ID
     * @param storeId 门店ID
     * @return 商品详情及客制化数据，商品全局下架时返回错误
     */
    @GetMapping("/{id}")
    public ApiResponse<ProductBizDetailResponse> getProductDetail(
            @PathVariable("id") @Min(value = 1, message = "商品ID不能为空") Long id,
            @RequestParam("storeId") @Min(value = 1, message = "门店ID不能为空") Long storeId) {
        return productBizService.getProductDetail(id, storeId);
    }

    /**
     * 更新商品的门店销售状态
     * @param id 商品ID
     * @param request 门店ID与在售状态
     * @return 操作结果
     */
    @PutMapping("/{id}/sale")
    public ApiResponse<Void> updateSaleStatus(
            @PathVariable("id") @Min(value = 1, message = "商品ID不能为空") Long id,
            @Valid @RequestBody UpdateStoreProductSaleRequest request) {
        return productBizService.updateSaleStatus(id, request);
    }
}
