package cn.dextea.store.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.store.dto.request.NearbyStoreRequest;
import cn.dextea.store.dto.response.NearbyStoreResponse;

import java.util.List;

public interface StoreBizService {

    /**
     * 推荐附近门店
     *
     * @param request 包含用户经纬度和搜索参数的请求
     * @return 按距离排序的附近门店列表
     */
    ApiResponse<List<NearbyStoreResponse>> getNearbyStores(NearbyStoreRequest request);
}
