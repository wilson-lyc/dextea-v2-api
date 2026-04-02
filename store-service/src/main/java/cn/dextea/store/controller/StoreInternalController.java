package cn.dextea.store.controller;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.api.dto.response.StoreValidityResponse;
import cn.dextea.store.service.StoreInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Min;

@RestController
@RequestMapping("/v1/internal/stores")
@RequiredArgsConstructor
@Validated
public class StoreInternalController {
    private final StoreInternalService storeInternalService;

    /**
     * 校验门店是否有效
     * @param id 门店ID
     * @return 校验结果
     */
    @GetMapping("/{id}/validity")
    public ApiResponse<StoreValidityResponse> checkStoreValidity(
            @PathVariable("id") @Min(value = 1, message = "门店ID不能为空") Long id) {
        return storeInternalService.checkValidity(id);
    }
}
