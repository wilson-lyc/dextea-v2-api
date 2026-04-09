package cn.dextea.store.service.impl;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.store.StoreModel;
import cn.dextea.store.code.StoreErrorCode;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.model.NearbyStoreModel;
import cn.dextea.store.service.CustomerService;
import cn.dextea.store.util.RedisUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private RedisUtil redisUtil;
    @Resource
    private StoreMapper storeMapper;
    @Override
    public DexteaApiResponse<List<cn.dextea.common.model.store.StoreModel>> getNearbyStore(Double longitude, Double latitude, Integer radius, Integer limit) {
        // 获取附近门店
        List<NearbyStoreModel> nearbyStores =redisUtil.getNearbyStores(longitude,latitude,radius,limit);
        // 提取所有门店ID
        List<Long> storeIds = nearbyStores.stream()
                .map(NearbyStoreModel::getId)
                .filter(Objects::nonNull)
                .toList();
        // 查询门店详情
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, cn.dextea.common.model.store.StoreModel.class)
                .in(Store::getId,storeIds);
        List<cn.dextea.common.model.store.StoreModel> stores=storeMapper.selectJoinList(cn.dextea.common.model.store.StoreModel.class,wrapper);
        // 构建结果集
        for (cn.dextea.common.model.store.StoreModel store : stores) {
            NearbyStoreModel nearbyStore = nearbyStores.stream()
                    .filter(item -> item.getId().equals(store.getId()))
                    .findFirst()
                    .orElse(null);
            if (nearbyStore != null) {
                store.setDistance(nearbyStore.getDistance());
                store.setDistanceUnit(nearbyStore.getDistanceUnit());
            }
        }
        return DexteaApiResponse.success(stores);
    }

    @Override
    public DexteaApiResponse<StoreModel> getStoreDetail(Long id, Double longitude, Double latitude) throws NotFoundException {
        // 获取门店信息
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, StoreModel.class)
                .eq(Store::getId,id);
        StoreModel store=storeMapper.selectJoinOne(StoreModel.class,wrapper);
        if (Objects.isNull(store))
            throw new NotFoundException("门店不存在");
        // 计算距离
        if(Objects.nonNull(latitude)&&Objects.nonNull(longitude)){
            NearbyStoreModel distance=redisUtil.getDistanceToStore(id,longitude,latitude);
            store.setDistance(distance.getDistance());
            store.setDistanceUnit(distance.getDistanceUnit());
        }
        return DexteaApiResponse.success(store);
    }

    @Override
    public DexteaApiResponse<List<StoreModel>> searchStore(String name) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, StoreModel.class)
                .like(Store::getName,name);
        List<StoreModel> list=storeMapper.selectJoinList(StoreModel.class,wrapper);
        if(list.isEmpty()){
            return DexteaApiResponse.notFound(StoreErrorCode.STORE_CUSTOMER_SEARCH_NULL.getCode(),
                    StoreErrorCode.STORE_CUSTOMER_SEARCH_NULL.getMsg());
        }
        return DexteaApiResponse.success(list);
    }
}
