package cn.dextea.cart.client;

import cn.dextea.cart.client.dto.request.BatchStoreAvailabilityRequest;
import cn.dextea.cart.client.dto.request.CartSnapshotRequest;
import cn.dextea.cart.client.dto.response.CartSnapshotResponse;
import cn.dextea.cart.client.dto.response.ProductStoreAvailabilityResponse;
import cn.dextea.common.web.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "product-service")
public interface ProductInternalClient {

    @PostMapping("/v1/internal/products/cart-snapshot")
    ApiResponse<CartSnapshotResponse> getCartSnapshot(@RequestBody CartSnapshotRequest request);

    @PostMapping("/v1/internal/products/store-availability")
    ApiResponse<List<ProductStoreAvailabilityResponse>> checkStoreAvailability(
            @RequestBody BatchStoreAvailabilityRequest request);
}
