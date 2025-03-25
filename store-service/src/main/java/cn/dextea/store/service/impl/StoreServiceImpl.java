package cn.dextea.store.service.impl;

import cn.dextea.common.code.StoreStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.OptionDTO;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.store.dto.*;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.common.pojo.Store;
import cn.dextea.store.pojo.Location;
import cn.dextea.store.pojo.License;
import cn.dextea.store.service.StoreService;
import cn.dextea.store.util.RedisUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

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
    @Resource
    StoreFeign storeFeign;

    @Override
    public ApiResponse createStore(StoreCreateDTO data) {
        Store store=data.toStore();
        // 获取经纬度坐标
        HttpResponse res = HttpRequest.get("https://restapi.amap.com/v3/geocode/geo")
                    .form("key", "ba9e3a30dede2dbb9f6f6fd97f1b4fd1")
                    .form("address", store.getAddress())
                    .form("city", store.getCity())
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
        return ApiResponse.success("门店创建成功", JSONObject.of("store",store));
    }

    @Override
    public ApiResponse getStoreList(int current, int size, StoreFilter filter) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class,StoreListDTO.class)
                .eqIfExists(Store::getId,filter.getId())
                .likeIfExists(Store::getName,filter.getName())
                .eqIfExists(Store::getStatus,filter.getStatus())
                .eqIfExists(Store::getProvince,filter.getProvince())
                .eqIfExists(Store::getCity,filter.getCity())
                .eqIfExists(Store::getDistrict,filter.getDistrict())
                .eqIfExists(Store::getLinkman,filter.getLinkman())
                .eqIfExists(Store::getPhone,filter.getPhone());
        IPage<StoreListDTO> page = storeMapper.selectJoinPage(
                new Page<>(current,size),
                StoreListDTO.class,
                wrapper);
        if (page.getCurrent()>page.getPages()){
            page = storeMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    StoreListDTO.class,
                    wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ApiResponse getStoreOption(Integer status) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAs(Store::getId, OptionDTO::getValue)
                .selectAs(Store::getName, OptionDTO::getLabel)
                .eqIfExists(Store::getStatus,status);
        List<OptionDTO> options=storeMapper.selectJoinList(OptionDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("count",options.size(),"options",options));
    }

    @Override
    public ApiResponse getStoreBase(Long id) throws NotFoundException {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, StoreBaseDTO.class)
                .eq(Store::getId,id);
        StoreBaseDTO store=storeMapper.selectJoinOne(StoreBaseDTO.class, wrapper);
        if (Objects.isNull(store)){
            throw new NotFoundException("不存在该门店");
        }
        return ApiResponse.success(JSONObject.of("store",store));
    }

    @Override
    public ApiResponse getStoreLicense(Long id) throws NotFoundException {
        Store store=storeMapper.selectById(id);
        if (Objects.isNull(store)){
            throw new NotFoundException("不存在该门店");
        }
        JSONArray licenses=new JSONArray();
        // 营业执照
        License business= License.builder()
                .key("businessLicense")
                .name("营业执照")
                .action("/store/upload/business-license")
                .url(store.getBusinessLicense())
                .build();
        licenses.add(business);
        // 食品许可证
        License food= License.builder()
                .key("foodBusinessLicense")
                .name("食品许可证")
                .action("/store/upload/food-license")
                .url(store.getFoodLicense())
                .build();
        licenses.add(food);
        return ApiResponse.success(JSONObject.of("licenses",licenses));
    }

    @Override
    public ApiResponse getStoreStatus(Long id) throws NotFoundException {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .select(Store::getStatus)
                .eq(Store::getId,id);
        Integer status=storeMapper.selectJoinOne(Integer.class,wrapper);
        if(Objects.isNull(status))
            throw new NotFoundException("不存在该门店");
        return ApiResponse.success(JSONObject.of("status",status));
    }

    @Override
    public ApiResponse getStoreLocation(Long id) throws NotFoundException {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class, Location.class)
                .eq(Store::getId,id);
        Location location=storeMapper.selectJoinOne(Location.class,wrapper);
        if (Objects.isNull(location))
            throw new NotFoundException("不存在该门店");
        return ApiResponse.success(JSONObject.of("location",location));
    }

    @Override
    public ApiResponse updateStoreBase(Long id, StoreUpdateBaseDTO data) throws NotFoundException {
        Store store=data.toStore();
        store.setId(id);
        if (storeMapper.updateById(store)==0)
            throw new NotFoundException("不存在该门店");
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateStoreLocation(Long id, StoreUpdateLocationDTO data) throws NotFoundException {
        // 校验ID
        if(!storeFeign.isStoreIdValid(id))
            throw new NotFoundException("不存在该门店");
        Store store=Store.builder()
                .id(id)
                .longitude(data.getLongitude())
                .latitude(data.getLatitude())
                .build();
        // 更新mysql
        storeMapper.updateById(store);
        // 更新redis
        redisUtil.setStoreLocation(id,data.getLongitude(),data.getLatitude());
        return ApiResponse.success("定位更新成功");
    }

    @Override
    public ApiResponse updateStoreStatus(Long id, Integer status) throws NotFoundException {
        Store store=Store.builder()
                .id(id)
                .status(status)
                .build();
        if (storeMapper.updateById(store)==0)
            throw new NotFoundException("不存在该门店");
        return ApiResponse.success("状态更新成功");
    }
}
