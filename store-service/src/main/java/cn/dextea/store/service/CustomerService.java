package cn.dextea.store.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.store.StoreModel;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<List<StoreModel>> getNearbyStore(Double longitude, Double latitude, Integer radius, Integer limit);
    DexteaApiResponse<StoreModel> getStoreDetail(Long id, Double longitude, Double latitude) throws NotFoundException;
}
