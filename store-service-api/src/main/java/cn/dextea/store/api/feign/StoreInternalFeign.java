package cn.dextea.store.api.feign;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.api.dto.response.StoreValidityResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "store-service")
public interface StoreInternalFeign {
    @GetMapping("/v1/internal/stores/{id}/validity")
    ApiResponse<StoreValidityResponse> checkStoreValidity(@PathVariable("id") Long id);
}
