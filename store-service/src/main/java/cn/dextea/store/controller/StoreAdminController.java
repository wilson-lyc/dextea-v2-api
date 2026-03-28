package cn.dextea.store.controller;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.dto.request.CreateStoreRequest;
import cn.dextea.store.dto.request.StorePageQueryRequest;
import cn.dextea.store.dto.request.UpdateStoreRequest;
import cn.dextea.store.dto.response.StoreDetailResponse;
import cn.dextea.store.service.StoreAdminService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 门店管理
 */
@RestController
@RequestMapping("/v1/admin/stores")
@RequiredArgsConstructor
@Validated
public class StoreAdminController {
    private final StoreAdminService storeAdminService;

    /**
     * 创建门店
     * @param request 创建门店请求参数
     * @return 门店详情
     */
    @PostMapping
    public ApiResponse<StoreDetailResponse> createStore(@Valid @RequestBody CreateStoreRequest request) {
        return storeAdminService.createStore(request);
    }

    /**
     * 分页查询门店
     * @param request 门店分页查询参数
     * @return 门店分页结果
     */
    @GetMapping
    public ApiResponse<IPage<StoreDetailResponse>> getStorePage(@Valid StorePageQueryRequest request) {
        return storeAdminService.getStorePage(request);
    }

    /**
     * 查询门店详情
     * @param id 门店ID
     * @return 门店详情
     */
    @GetMapping("/{id}")
    public ApiResponse<StoreDetailResponse> getStoreDetail(
            @PathVariable("id") @Min(value = 1, message = "门店ID不能为空") Long id) {
        return storeAdminService.getStoreDetail(id);
    }

    /**
     * 更新门店
     * @param id 门店ID
     * @param request 更新门店请求参数
     * @return 更新后的门店详情
     */
    @PutMapping("/{id}")
    public ApiResponse<StoreDetailResponse> updateStore(
            @PathVariable("id") @Min(value = 1, message = "门店ID不能为空") Long id,
            @Valid @RequestBody UpdateStoreRequest request) {
        return storeAdminService.updateStore(id, request);
    }

    /**
     * 删除门店
     * @param id 门店ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteStore(
            @PathVariable("id") @Min(value = 1, message = "门店ID不能为空") Long id) {
        return storeAdminService.deleteStore(id);
    }
}
