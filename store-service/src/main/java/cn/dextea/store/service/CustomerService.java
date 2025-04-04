package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.store.dto.GetStoreDetailResponse;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    ApiResponse getNearbyStore(Double longitude, Double latitude, Integer radius, Integer limit);
    DexteaApiResponse<GetStoreDetailResponse> getStoreDetail(Long id, Double longitude, Double latitude) throws NotFoundException;
}
