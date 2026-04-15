package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.StoreProductPageRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
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
     * @param request 门店ID、商品名、商品门店状态、分页参数
     * @return 商品分页列表
     */
    @Operation(summary = "分页查询商品列表（门店端）", description = "适用于门店端分页查询商品数据")
    @GetMapping
    public ApiResponse<IPage<ProductDetailResponse>> getPage(
            @Valid StoreProductPageRequest request) {
        return productBizService.getPage(request);
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
}
