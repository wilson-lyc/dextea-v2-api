package cn.dextea.product.controller;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BatchStoreAvailabilityRequest;
import cn.dextea.product.dto.request.CartSnapshotRequest;
import cn.dextea.product.dto.response.CartSnapshotResponse;
import cn.dextea.product.dto.response.ProductStoreAvailabilityResponse;
import cn.dextea.product.service.ProductInternalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/v1/internal/products")
@RequiredArgsConstructor
@Validated
public class ProductInternalController {

    private final ProductInternalService productInternalService;

    /**
     * 获取商品快照（供购物车加购时使用）
     * @param request 商品ID及所选选项ID列表
     * @return 商品名称、基础价格及各选项快照
     */
    @PostMapping("/cart-snapshot")
    public ApiResponse<CartSnapshotResponse> getCartSnapshot(
            @Valid @RequestBody CartSnapshotRequest request) {
        return productInternalService.getCartSnapshot(request);
    }

    /**
     * 批量查询商品及选项在指定门店的可用状态（供购物车切换门店时使用）
     * @param request 门店ID及商品列表
     * @return 各商品及选项在目标门店的可用状态
     */
    @PostMapping("/store-availability")
    public ApiResponse<List<ProductStoreAvailabilityResponse>> checkStoreAvailability(
            @Valid @RequestBody BatchStoreAvailabilityRequest request) {
        return productInternalService.checkStoreAvailability(request);
    }
}
