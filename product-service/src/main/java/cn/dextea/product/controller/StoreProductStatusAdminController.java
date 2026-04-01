package cn.dextea.product.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.product.dto.request.BatchSetStoreProductStatusRequest;
import cn.dextea.product.dto.request.SetStoreProductStatusRequest;
import cn.dextea.product.dto.response.StoreProductStatusDetailResponse;
import cn.dextea.product.service.StoreProductStatusAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/admin/store-product-status")
@RequiredArgsConstructor
@SaCheckLogin
@Validated
public class StoreProductStatusAdminController {

    private final StoreProductStatusAdminService storeProductStatusAdminService;

    /**
     * 设置门店商品状态
     * @param request 设置门店商品状态请求参数
     * @return 门店商品状态详情
     */
    @PostMapping
    public ApiResponse<StoreProductStatusDetailResponse> setStatus(
            @Valid @RequestBody SetStoreProductStatusRequest request) {
        return storeProductStatusAdminService.setStatus(request);
    }

    /**
     * 获取门店商品状态
     * @param storeId 门店ID
     * @param productId 商品ID
     * @return 门店商品状态详情
     */
    @GetMapping
    public ApiResponse<StoreProductStatusDetailResponse> getStatus(
            @RequestParam @Min(value = 1, message = "门店ID无效") Long storeId,
            @RequestParam @Min(value = 1, message = "商品ID无效") Long productId) {
        return storeProductStatusAdminService.getStatus(storeId, productId);
    }

    /**
     * 分页查询门店商品状态列表
     * @param storeId 门店ID（可选）
     * @param productId 商品ID（可选）
     * @param current 当前页码
     * @param size 每页条数
     * @return 门店商品状态分页列表
     */
    @GetMapping("/page")
    public ApiResponse<IPage<StoreProductStatusDetailResponse>> getStatusPage(
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "1") Long current,
            @RequestParam(defaultValue = "10") Long size) {
        return storeProductStatusAdminService.getStatusPageByStore(storeId, productId, current, size);
    }

    /**
     * 批量设置门店商品状态
     * @param request 批量设置门店商品状态请求参数
     * @return 操作结果
     */
    @PostMapping("/batch")
    public ApiResponse<Void> batchSetStatus(
            @Valid @RequestBody BatchSetStoreProductStatusRequest request) {
        return storeProductStatusAdminService.batchSetStatus(request);
    }
}