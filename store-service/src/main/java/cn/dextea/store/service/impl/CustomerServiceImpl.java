package cn.dextea.store.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.StoreInfoDTO;
import cn.dextea.store.dto.StoreNearbyDTO;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.common.pojo.Store;
import cn.dextea.store.service.CustomerService;
import cn.dextea.store.util.RedisUtil;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

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
    public ApiResponse getNearbyStore(Double longitude, Double latitude, Integer radius, Integer limit) {
        List<StoreNearbyDTO> nearbyStores=redisUtil.getNearbyStores(longitude,latitude,radius,limit);
        for(StoreNearbyDTO store:nearbyStores){
            Store s=storeMapper.selectById(store.getId());
            BeanUtils.copyProperties(s, store);
        }
        return ApiResponse.success(JSONObject.of(
                "counts",nearbyStores.size(),
                "stores",nearbyStores));
    }

    @Override
    public ApiResponse getStoreInfo(Long id, Double longitude, Double latitude) {
        // 获取门店信息
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, StoreInfoDTO.class)
                .eq(Store::getId,id);
        StoreInfoDTO store=storeMapper.selectJoinOne(StoreInfoDTO.class,wrapper);
        if (store==null){
            return ApiResponse.notFound(String.format("不存在ID=%d的门店",id));
        }
        // 计算距离
        if(latitude!=null&&longitude!=null){
            double distance=redisUtil.getDistanceToStore(id,longitude,latitude);
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
        return ApiResponse.success(JSONObject.of("store",store));
    }
}
