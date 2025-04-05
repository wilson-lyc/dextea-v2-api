package cn.dextea.store.service;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.store.dto.GetStoreDetailResponse;
import cn.dextea.store.dto.StoreDetailResponse;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<List<StoreDetailResponse>> getNearbyStore(Double longitude, Double latitude, Integer radius, Integer limit);
    DexteaApiResponse<GetStoreDetailResponse> getStoreDetail(Long id, Double longitude, Double latitude) throws NotFoundException;
}
