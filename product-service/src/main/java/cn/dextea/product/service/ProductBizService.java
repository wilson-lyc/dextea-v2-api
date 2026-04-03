package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.ProductBizPageQueryRequest;
import cn.dextea.product.dto.request.UpdateStoreProductStatusRequest;
import cn.dextea.product.dto.response.ProductBizPageItemResponse;
import cn.dextea.product.dto.response.StoreProductStatusDetailResponse;
import com.baomidou.mybatisplus.core.metadata.IPage;

public interface ProductBizService {

    /**
     * 分页查询全局可售商品列表，状态值反映商品在指定门店的可售状态
     * @param request 查询参数（门店ID、分页）
     * @return 商品分页数据
     */
    ApiResponse<IPage<ProductBizPageItemResponse>> getStoreProductPage(ProductBizPageQueryRequest request);

    /**
     * 更新商品在门店的销售状态
     * @param productId 商品ID
     * @param storeId 门店ID
     * @param request 包含目标销售状态
     * @return 更新后的门店商品状态详情
     */
    ApiResponse<StoreProductStatusDetailResponse> updateStoreStatus(Long productId, Long storeId, UpdateStoreProductStatusRequest request);
}
