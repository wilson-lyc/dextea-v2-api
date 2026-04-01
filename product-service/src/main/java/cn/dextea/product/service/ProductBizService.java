package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.GetProductBizDetailRequest;
import cn.dextea.product.dto.response.ProductBizDetailResponse;

public interface ProductBizService {

    /**
     * 获取商品详情（包含客制化项目及选项）
     * @param request 请求参数（门店ID、商品ID）
     * @return 商品详情信息
     */
    ApiResponse<ProductBizDetailResponse> getProductBizDetail(GetProductBizDetailRequest request);
}
