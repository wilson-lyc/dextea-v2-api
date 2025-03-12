package cn.dextea.store.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.*;
import cn.dextea.store.feign.ProductFeign;
import cn.dextea.store.feign.TosFeign;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.service.StoreService;
import cn.dextea.store.util.RedisUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Service
public class StoreServiceImpl implements StoreService {
    @Resource
    StoreMapper storeMapper;
    @Resource
    RedisUtil redisUtil;

    @Override
    public ApiResponse createStore(StoreCreateDTO data) {
        Store store=data.toStore();
        store.setStatus(0);// 状态默认为未激活
        // 获取门店定位 - 高德api
        HttpResponse res= HttpRequest.get("https://restapi.amap.com/v3/geocode/geo")
                .form("key","ba9e3a30dede2dbb9f6f6fd97f1b4fd1")
                .form("address",store.getAddress())
                .form("city",store.getCity())
                .execute();
        String location=JSONObject.parseObject(res.body())
                .getJSONArray("geocodes")
                .getJSONObject(0)
                .getString("location");
        String[] parts = location.split(",");
        store.setLongitude(Double.parseDouble(parts[0]));
        store.setLatitude(Double.parseDouble(parts[1]));
        // 写入db
        storeMapper.insert(store);
        // 定位写入redis
        redisUtil.setStoreLocation(store.getId(),store.getLongitude(), store.getLatitude());
        return ApiResponse.success("门店创建成功");
    }

    @Override
    public ApiResponse getStoreList(int current, int size, StoreFilter filter) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class,StoreListDTO.class)
                .eq(filter.getId()!=null,Store::getId,filter.getId())
                .like(StringUtils.isNotBlank(filter.getName()),Store::getName,filter.getName())
                .eq(filter.getStatus()!=null,Store::getStatus,filter.getStatus())
                .eq(StringUtils.isNotBlank(filter.getProvince()),Store::getProvince,filter.getProvince())
                .eq(StringUtils.isNotBlank(filter.getCity()),Store::getCity,filter.getCity())
                .eq(StringUtils.isNotBlank(filter.getDistrict()),Store::getDistrict,filter.getDistrict())
                .eq(StringUtils.isNotBlank(filter.getLinkman()),Store::getLinkman,filter.getLinkman())
                .eq(StringUtils.isNotBlank(filter.getPhone()),Store::getPhone,filter.getPhone());
        Page<Store> page = new Page<>(current, size);
        page=storeMapper.selectJoinPage(page,wrapper);
        if (page.getCurrent()>page.getPages()){
            page.setCurrent(page.getPages());
            page=storeMapper.selectJoinPage(page,wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ApiResponse getStoreOption(Integer status) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAs(Store::getId, StoreOptionDTO::getValue)
                .selectAs(Store::getName, StoreOptionDTO::getLabel)
                .eq(status!=null,Store::getStatus,status);
        List<StoreOptionDTO> options=storeMapper.selectJoinList(StoreOptionDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("options",options,"count",options.size()));
    }

    @Override
    public ApiResponse getStoreBase(Long id) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, StoreBaseDTO.class)
                .eq(Store::getId,id);
        StoreBaseDTO store=storeMapper.selectJoinOne(StoreBaseDTO.class, wrapper);
        if (store==null){
            return ApiResponse.notFound(String.format("不存在ID=%d的门店",id));
        }
        return ApiResponse.success(JSONObject.of("store",store));
    }

    @Override
    public ApiResponse getStoreLicense(Long id) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class,StoreLicenseDTO.class)
                .eq(Store::getId,id);
        StoreLicenseDTO license=storeMapper.selectJoinOne(StoreLicenseDTO.class,wrapper);
        if (license==null){
            return ApiResponse.notFound(String.format("不存在ID=%d的门店",id));
        }
        return ApiResponse.success(JSONObject.of("license",license));
    }

    @Override
    public ApiResponse getStoreStatus(Long id) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, StoreStatusDTO.class)
                .eq(Store::getId,id);
        StoreStatusDTO status=storeMapper.selectJoinOne(StoreStatusDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("store",status));
    }

    @Override
    public ApiResponse getStoreLocation(Long id) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class,StoreLocationDTO.class)
                .eq(Store::getId,id);
        StoreLocationDTO location=storeMapper.selectJoinOne(StoreLocationDTO.class,wrapper);
        if (location==null){
            return ApiResponse.notFound(String.format("不存在ID=%d的门店",id));
        }
        return ApiResponse.success(JSONObject.of("location",location));
    }

    @Override
    public ApiResponse updateStoreBase(Long id, StoreUpdateBaseDTO data) {
        Store store=data.toStore();
        store.setId(id);
        int num=storeMapper.updateById(store);
        if (num==0){
            return ApiResponse.notFound("更新失败");
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse updateStoreLocation(Long id, StoreUpdateLocationDTO body) {
        Store store=Store.builder()
                .id(id)
                .longitude(body.getLongitude())
                .latitude(body.getLatitude())
                .build();
        storeMapper.updateById(store);// 更新db
        redisUtil.setStoreLocation(id,body.getLongitude(),body.getLatitude());// 更新redis
        return ApiResponse.success("定位更新成功");
    }

    @Override
    public ApiResponse updateStoreStatus(Long id, StoreUpdateStatusDTO body) {
        Store store=Store.builder()
                .id(id)
                .status(body.getStatus())
                .build();
        int num=storeMapper.updateById(store);
        if (num==0){
            return ApiResponse.notFound("更新失败");
        }
        return ApiResponse.success();
    }
}
