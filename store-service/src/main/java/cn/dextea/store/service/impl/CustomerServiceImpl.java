package cn.dextea.store.service.impl;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.store.dto.GetStoreDetailResponse;
import cn.dextea.store.dto.StoreDetailResponse;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.common.pojo.Store;
import cn.dextea.store.pojo.NearbyStore;
import cn.dextea.store.service.CustomerService;
import cn.dextea.store.util.RedisUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
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
    public DexteaApiResponse<List<StoreDetailResponse>> getNearbyStore(Double longitude, Double latitude, Integer radius, Integer limit) {
        // 获取附近门店
        List<NearbyStore> nearbyStores=redisUtil.getNearbyStores(longitude,latitude,radius,limit);
        // 提取所有门店ID
        List<Long> storeIds = nearbyStores.stream()
                .map(NearbyStore::getId)
                .filter(Objects::nonNull)
                .toList();
        // 查询门店
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, StoreDetailResponse.class)
                .in(Store::getId,storeIds);
        List<StoreDetailResponse> storeDetails=storeMapper.selectJoinList(StoreDetailResponse.class,wrapper);
        // 构建结果集
        for (StoreDetailResponse store : storeDetails) {
            NearbyStore nearbyStore = nearbyStores.stream()
                    .filter(item -> item.getId().equals(store.getId()))
                    .findFirst()
                    .orElse(null);
            if (nearbyStore != null) {
                store.setDistance(nearbyStore.getDistance());
                store.setDistanceUnit(nearbyStore.getDistanceUnit());
            }
        }
        return DexteaApiResponse.success(storeDetails);
    }

    @Override
    public DexteaApiResponse<GetStoreDetailResponse> getStoreDetail(Long id, Double longitude, Double latitude) throws NotFoundException {
        // 获取门店信息
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, GetStoreDetailResponse.class)
                .eq(Store::getId,id);
        GetStoreDetailResponse store=storeMapper.selectJoinOne(GetStoreDetailResponse.class,wrapper);
        if (Objects.isNull(store))
            throw new NotFoundException("门店不存在");
        // 计算距离
        if(Objects.nonNull(latitude)&&Objects.nonNull(longitude)){
            double distance=redisUtil.getDistanceToStore(id,longitude,latitude);
            // 计算展示单位
            if(distance<1){
                DecimalFormat df = new DecimalFormat("#");
                distance = Double.parseDouble(df.format(distance*1000));
                store.setDistanceUnit("m");
            }else{
                DecimalFormat df = new DecimalFormat("#.0");
                distance = Double.parseDouble(df.format(distance));
                store.setDistanceUnit("km");
            }
            store.setDistance(distance);
        }
        return DexteaApiResponse.success(store);
    }
}
