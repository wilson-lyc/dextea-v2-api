package cn.dextea.store.controller;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.dto.request.NearbyStoreRequest;
import cn.dextea.store.dto.response.NearbyStoreResponse;
import cn.dextea.store.service.StoreBizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 门店业务接口
 */
@RestController
@RequestMapping("/v1/biz/stores")
@RequiredArgsConstructor
@Validated
public class StoreBizController {

    private final StoreBizService storeBizService;

    /**
     * 推荐附近门店
     * @param request 包含用户经纬度和搜索参数
     * @return 按距离排序的附近门店列表
     */
    @GetMapping("/nearby")
    public ApiResponse<List<NearbyStoreResponse>> getNearbyStores(
            @Valid NearbyStoreRequest request) {
        return storeBizService.getNearbyStores(request);
    }
}
