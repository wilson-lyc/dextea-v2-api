package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.GetProductBizDetailRequest;
import cn.dextea.product.dto.response.ProductBizDetailResponse;
import cn.dextea.product.service.ProductBizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/biz/products")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class ProductBizController {

    private final ProductBizService productBizService;

    /**
     * 获取商品详情（包含客制化项目及选项）
     * @param request 请求参数（门店ID、商品ID）
     * @return 商品详情信息
     */
    @PostMapping("/detail")
    public ApiResponse<ProductBizDetailResponse> getProductBizDetail(
            @Valid @RequestBody GetProductBizDetailRequest request) {
        return productBizService.getProductBizDetail(request);
    }
}