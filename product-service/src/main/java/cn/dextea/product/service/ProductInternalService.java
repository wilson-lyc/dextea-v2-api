package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BatchStoreAvailabilityRequest;
import cn.dextea.product.dto.request.CartSnapshotRequest;
import cn.dextea.product.dto.response.CartSnapshotResponse;
import cn.dextea.product.dto.response.ProductStoreAvailabilityResponse;

import java.util.List;

public interface ProductInternalService {

    ApiResponse<CartSnapshotResponse> getCartSnapshot(CartSnapshotRequest request);

    ApiResponse<List<ProductStoreAvailabilityResponse>> checkStoreAvailability(BatchStoreAvailabilityRequest request);
}
