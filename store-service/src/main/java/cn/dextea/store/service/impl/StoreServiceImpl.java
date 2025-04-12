package cn.dextea.store.service.impl;

import cn.dextea.common.code.StoreStatus;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.model.common.ImageModel;
import cn.dextea.common.model.common.SelectOptionModel;
import cn.dextea.common.model.store.StoreModel;
import cn.dextea.store.code.StoreErrorCode;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.model.*;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.service.StoreService;
import cn.dextea.store.util.RedisUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Service
@RefreshScope
public class StoreServiceImpl implements StoreService {
    @Resource
    StoreMapper storeMapper;
    @Resource
    RedisUtil redisUtil;
    @Value("${amap.key}")
    private String AMAP_KEY;

    @Override
    public DexteaApiResponse<Void> createStore(StoreCreateRequest data) {
        Store store=data.toStore();
        // 门店默认未激活
        store.setStatus(StoreStatus.NOT_ACTIVE.getValue());
        // 获取经纬度坐标
        try{
            HttpResponse res = HttpRequest.get("https://restapi.amap.com/v3/geocode/geo")
                    .form("key", AMAP_KEY)
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
        }catch (Exception e){
            throw new RuntimeException("获取经纬度坐标失败");
        }
        // 写入数据库
        storeMapper.insert(store);
        // 定位写入redis
        redisUtil.setStoreLocation(store.getId(),store.getLongitude(), store.getLatitude());
        return DexteaApiResponse.success("门店创建成功");
    }

    @Override
    public DexteaApiResponse<IPage<StoreModel>> getStoreList(int current, int size, StoreFilter filter) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class,StoreModel.class)
                .eqIfExists(Store::getId,filter.getId())
                .likeIfExists(Store::getName,filter.getName())
                .eqIfExists(Store::getStatus,filter.getStatus())
                .eqIfExists(Store::getProvince,filter.getProvince())
                .eqIfExists(Store::getCity,filter.getCity())
                .eqIfExists(Store::getDistrict,filter.getDistrict())
                .eqIfExists(Store::getLinkman,filter.getLinkman())
                .eqIfExists(Store::getPhone,filter.getPhone());
        IPage<StoreModel> page = storeMapper.selectJoinPage(
                new Page<>(current,size),
                StoreModel.class,
                wrapper);
        if (page.getCurrent()>page.getPages()){
            page = storeMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    StoreModel.class,
                    wrapper);
        }
        return DexteaApiResponse.success(page);
    }

    @Override
    public DexteaApiResponse<List<SelectOptionModel>> getStoreOption() {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAs(Store::getId, SelectOptionModel::getValue)
                .selectAs(Store::getName, SelectOptionModel::getLabel);
        List<SelectOptionModel> options=storeMapper.selectJoinList(SelectOptionModel.class,wrapper);
        return DexteaApiResponse.success(options);
    }

    @Override
    public DexteaApiResponse<JSONArray> getStoreTreeOption() {
        JSONArray res=new JSONArray();
        MPJLambdaWrapper<Store> provinceWrapper=new MPJLambdaWrapper<Store>()
                .select(Store::getProvince)
                .groupBy(Store::getProvince);
        List<String> provinceList=storeMapper.selectJoinList(String.class, provinceWrapper);
        for(String province:provinceList){
            JSONObject provinceJson=JSONObject.of("value",province,"label",province);
            MPJLambdaWrapper<Store> cityWrapper=new MPJLambdaWrapper<Store>()
                    .select(Store::getCity)
                    .eq(Store::getProvince,province)
                    .groupBy(Store::getCity);
            List<String> cityList=storeMapper.selectJoinList(String.class, cityWrapper);
            JSONArray provinceChildren=new JSONArray();
            for(String city:cityList){
                JSONObject cityJson=JSONObject.of("value",city,"label",city);
                MPJLambdaWrapper<Store> districtWrapper=new MPJLambdaWrapper<Store>()
                        .select(Store::getDistrict)
                        .eq(Store::getProvince,province)
                        .eq(Store::getCity,city)
                        .groupBy(Store::getDistrict);
                List<String> districtList=storeMapper.selectJoinList(String.class, districtWrapper);
                JSONArray cityChildren=new JSONArray();
                if(Objects.nonNull(districtList.get(0))){
                    // 分区读取门店
                    for (String district:districtList){
                        JSONObject districtJson=JSONObject.of("value",district,"label",district);
                        MPJLambdaWrapper<Store> storeWrapper=new MPJLambdaWrapper<Store>()
                                .selectAs(Store::getId, TreeOptionModel::getValue)
                                .selectAs(Store::getName, TreeOptionModel::getLabel)
                                .eq(Store::getProvince,province)
                                .eq(Store::getCity,city)
                                .eq(Store::getDistrict,district);
                        List<TreeOptionModel> stores=storeMapper.selectJoinList(TreeOptionModel.class, storeWrapper);
                        districtJson.put("children",stores);
                        cityChildren.add(districtJson);
                    }
                }else{
                    // 直辖市没有区
                    MPJLambdaWrapper<Store> storeWrapper=new MPJLambdaWrapper<Store>()
                            .selectAs(Store::getId, TreeOptionModel::getValue)
                            .selectAs(Store::getName, TreeOptionModel::getLabel)
                            .eq(Store::getProvince,province)
                            .eq(Store::getCity,city);
                    List<TreeOptionModel> stores=storeMapper.selectJoinList(TreeOptionModel.class, storeWrapper);
                    cityChildren.addAll(stores);
                }
                cityJson.put("children",cityChildren);
                provinceChildren.add(cityJson);
            }
            provinceJson.put("children",provinceChildren);
            res.add(provinceJson);
        }
        return DexteaApiResponse.success(res);
    }

    @Override
    public DexteaApiResponse<StoreModel> getStoreBase(Long id) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAs(Store::getId, StoreModel::getId)
                .selectAs(Store::getName, StoreModel::getName)
                .selectAs(Store::getProvince, StoreModel::getProvince)
                .selectAs(Store::getCity, StoreModel::getCity)
                .selectAs(Store::getDistrict, StoreModel::getDistrict)
                .selectAs(Store::getAddress, StoreModel::getAddress)
                .selectAs(Store::getLinkman, StoreModel::getLinkman)
                .selectAs(Store::getPhone,StoreModel::getPhone)
                .selectAs(Store::getOpenTime, StoreModel::getOpenTime)
                .selectAs(Store::getStatus,StoreModel::getStatus)
                .eq(Store::getId,id);
        StoreModel store=storeMapper.selectJoinOne(StoreModel.class, wrapper);
        if (Objects.isNull(store)){
            return DexteaApiResponse.notFound(StoreErrorCode.STORE_NOT_FOUND.getCode(),
                    StoreErrorCode.STORE_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(store);
    }

    @Override
    public DexteaApiResponse<List<ImageModel>> getStoreLicense(Long id){
        Store store=storeMapper.selectById(id);
        if (Objects.isNull(store)){
            return DexteaApiResponse.notFound(StoreErrorCode.STORE_NOT_FOUND.getCode(),
                    StoreErrorCode.STORE_NOT_FOUND.getMsg());
        }
        List<ImageModel> imageList=new ArrayList<>();
        // 营业执照
        ImageModel business= ImageModel.builder()
                .key("businessLicense")
                .name("营业执照")
                .action("/store/upload/business-license")
                .url(store.getBusinessLicense())
                .build();
        imageList.add(business);
        // 食品许可证
        ImageModel food= ImageModel.builder()
                .key("foodBusinessLicense")
                .name("食品许可证")
                .action("/store/upload/food-license")
                .url(store.getFoodLicense())
                .build();
        imageList.add(food);
        return DexteaApiResponse.success(imageList);
    }

    @Override
    public DexteaApiResponse<StoreModel> getStoreStatus(Long id){
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAs(Store::getStatus,StoreModel::getStatus)
                .eq(Store::getId,id);
        StoreModel store=storeMapper.selectJoinOne(StoreModel.class,wrapper);
        if(Objects.isNull(store)){
            return DexteaApiResponse.notFound(StoreErrorCode.STORE_NOT_FOUND.getCode(),
                    StoreErrorCode.STORE_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(store);
    }

    @Override
    public DexteaApiResponse<StoreModel> getStoreLocation(Long id){
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAs(Store::getLongitude, StoreModel::getLongitude)
                .selectAs(Store::getLatitude, StoreModel::getLatitude)
                .selectAs(Store::getProvince,StoreModel::getProvince)
                .selectAs(Store::getCity, StoreModel::getCity)
                .selectAs(Store::getDistrict, StoreModel::getDistrict)
                .selectAs(Store::getAddress, StoreModel::getAddress)
                .eq(Store::getId,id);
        StoreModel store=storeMapper.selectJoinOne(StoreModel.class,wrapper);
        if (Objects.isNull(store)){
            return DexteaApiResponse.notFound(StoreErrorCode.STORE_NOT_FOUND.getCode(),
                    StoreErrorCode.STORE_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(store);
    }

    @Override
    public DexteaApiResponse<Void> updateStoreBase(Long id, StoreUpdateBaseRequest data){
        UpdateWrapper<Store> updateWrapper=new UpdateWrapper<Store>()
                .eq("id",id)
                .set("name",data.getName())
                .set("province",data.getProvince())
                .set("city",data.getCity())
                .set("district",data.getDistrict())
                .set("address",data.getAddress())
                .set("linkman",data.getLinkman())
                .set("phone",data.getPhone())
                .set("status",data.getStatus())
                .set("open_time",data.getOpenTime());
        if (storeMapper.update(updateWrapper)==0){
            return DexteaApiResponse.notFound(StoreErrorCode.STORE_NOT_FOUND.getCode(),
                    StoreErrorCode.STORE_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success("更新成功");
    }

    @Override
    public DexteaApiResponse<Void> updateStoreLocation(Long id, StoreUpdateLocationRequest data){
        LambdaUpdateWrapper<Store> wrapper=new LambdaUpdateWrapper<Store>()
                .eq(Store::getId,id)
                .set(Store::getLongitude,data.getLongitude())
                .set(Store::getLatitude,data.getLatitude());
        // 更新数据库
        if(storeMapper.update(wrapper)==0){
            return DexteaApiResponse.notFound(StoreErrorCode.STORE_NOT_FOUND.getCode(),
                    StoreErrorCode.STORE_NOT_FOUND.getMsg());
        }
        // 更新redis
        redisUtil.setStoreLocation(id,data.getLongitude(),data.getLatitude());
        return DexteaApiResponse.success("定位更新成功");
    }

    @Override
    public DexteaApiResponse<Void> updateStoreStatus(Long id, Integer status){
        // 校验状态
        if(StoreStatus.fromValue(status).equals(StoreStatus.UNKNOWN)){
            return DexteaApiResponse.fail(StoreErrorCode.STORE_STATUS_ERROR.getCode(),
                    StoreErrorCode.STORE_STATUS_ERROR.getMsg());
        }
        LambdaUpdateWrapper<Store> wrapper=new LambdaUpdateWrapper<Store>()
                .eq(Store::getId,id)
                .set(Store::getStatus,status);
        if (storeMapper.update(wrapper)==0){
            return DexteaApiResponse.notFound(StoreErrorCode.STORE_NOT_FOUND.getCode(),
                    StoreErrorCode.STORE_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success("状态更新成功");
    }
}
