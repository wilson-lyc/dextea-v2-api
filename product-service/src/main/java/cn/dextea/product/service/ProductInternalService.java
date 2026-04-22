package cn.dextea.product.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.CheckStoreAvailabilityRequest;
import cn.dextea.product.dto.request.QueryCartSnapshotRequest;
import cn.dextea.product.dto.response.CartSnapshotResponse;
import cn.dextea.product.dto.response.ProductStoreAvailabilityResponse;

import java.util.List;

public interface ProductInternalService {

    ApiResponse<CartSnapshotResponse> getCartSnapshot(QueryCartSnapshotRequest request);

    ApiResponse<List<ProductStoreAvailabilityResponse>> checkStoreAvailability(CheckStoreAvailabilityRequest request);
}
