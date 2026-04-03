package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.ProductBizPageQueryRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.dto.response.ProductBizPageItemResponse;
import cn.dextea.product.dto.response.StoreProductStatusDetailResponse;
import cn.dextea.product.service.ProductBizService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/biz/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductBizController {

    private final ProductBizService productBizService;

    /**
     * 分页查询门店的商品列表
     * @param request 查询参数（门店ID、分页）
     * @return 商品分页数据
     */
    @GetMapping
    public ApiResponse<IPage<ProductBizPageItemResponse>> getStoreProductPage(@Valid ProductBizPageQueryRequest request) {
        return productBizService.getStoreProductPage(request);
    }

    /**
     * 更新商品在门店的销售状态
     * @param id 商品ID
     * @param storeId 门店ID
     * @param request 目标销售状态
     * @return 更新后的门店商品状态详情
     */
    @PutMapping("/{id}")
    public ApiResponse<StoreProductStatusDetailResponse> updateStoreStatus(
            @PathVariable("id") @Min(value = 1, message = "商品ID不能为空") Long id,
            @RequestParam("storeId") @Min(value = 1, message = "门店ID无效") Long storeId,
            @Valid @RequestBody UpdateStoreProductStatusRequest request) {
        return productBizService.updateStoreStatus(id, storeId, request);
    }
}