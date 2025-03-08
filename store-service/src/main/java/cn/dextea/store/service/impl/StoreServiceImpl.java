package cn.dextea.store.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.*;
import cn.dextea.store.feign.TosFeign;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.service.StoreService;
import cn.dextea.store.util.RedisUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
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
    TosFeign tosFeign;
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
    public ApiResponse getStoreBaseById(Long id) {
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
    public ApiResponse getStoreList(int current, int size, StoreFilter filter) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class,StoreListDTO.class);
        if(filter.getId()!=null){
            wrapper.eq(Store::getId,filter.getId());
        }
        if (filter.getName()!=null&&!filter.getName().isBlank()){
            wrapper.like(Store::getName,filter.getName());
        }
        if (filter.getStatus()!=null){
            wrapper.eq(Store::getStatus,filter.getStatus());
        }
        if (filter.getProvince()!=null&&!filter.getProvince().isBlank()){
            wrapper.eq(Store::getProvince,filter.getProvince());
        }
        if (filter.getCity()!=null&&!filter.getCity().isBlank()){
            wrapper.eq(Store::getCity,filter.getCity());
        }
        if (filter.getDistrict()!=null&&!filter.getDistrict().isBlank()){
            wrapper.eq(Store::getDistrict,filter.getDistrict());
        }
        if (filter.getLinkman()!=null&&!filter.getLinkman().isBlank()){
            wrapper.eq(Store::getLinkman,filter.getLinkman());
        }
        if (filter.getPhone()!=null&&!filter.getPhone().isBlank()) {
            wrapper.eq(Store::getPhone, filter.getPhone());
        }
        Page<Store> page = new Page<>(current, size);
        page=storeMapper.selectJoinPage(page,wrapper);
        // 如果当前页码大于总页数，返回最后一页
        if (page.getCurrent()>page.getPages()){
            page.setCurrent(page.getPages());
            page=storeMapper.selectJoinPage(page,wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ApiResponse updateStatus(Long id, Integer status) {
        UpdateWrapper<Store> wrapper=new UpdateWrapper<Store>()
                .eq("id",id)
                .set("status",status);
        int num=storeMapper.update(wrapper);
        if (num==0){
            return ApiResponse.notFound("更新失败");
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse updateBase(Long id, StoreUpdateDTO data) {
        Store store=data.toStore();
        store.setId(id);
        int num=storeMapper.updateById(store);
        if (num==0){
            return ApiResponse.notFound("更新失败");
        }
        return ApiResponse.success();
    }

    @Override
    public ResponseEntity<ApiResponse> uploadBusinessLicense(Long id, MultipartFile file) {
        // 查询旧的营业执照URL
        String oldUrl=storeMapper.selectById(id).getBusinessLicense();
        tosFeign.delete(oldUrl);
        // 上传新的营业执照
        String folder="store/license";
        String filename=String.format("%d_business",id);
        ApiResponse response=tosFeign.uploadWithCustomName(folder,filename,file);
        if (response.getCode()==200){
            String url=response.getData().getString("url");
            Store store=Store.builder()
                    .id(id)
                    .businessLicense(url)
                    .build();
            storeMapper.updateById(store);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
    }

    @Override
    public ResponseEntity<ApiResponse> uploadFoodLicense(Long id, MultipartFile file) {
        // 删除旧的食品经营许可证
        String oldUrl=storeMapper.selectById(id).getFoodBusinessLicense();
        tosFeign.delete(oldUrl);
        // 上传新的食品经营许可证
        String folder="store/license";
        String filename=String.format("%d_food",id);
        ApiResponse response=tosFeign.uploadWithCustomName(folder,filename,file);
        if (response.getCode()==200){
            String url=response.getData().getString("url");
            Store store=Store.builder()
                    .id(id)
                    .foodBusinessLicense(url)
                    .build();
            storeMapper.updateById(store);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body(ApiResponse.badRequest("上传失败"));
    }

    @Override
    public ApiResponse getSelectOptions() {
        List<StoreSelectOption> stores=storeMapper.getSelectOptions();
        return ApiResponse.success(JSONObject.of("options",stores));
    }

    @Override
    public ApiResponse updateLocation(Long id, Double longitude, Double latitude) {
        Store store=Store.builder()
                .id(id)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        storeMapper.updateById(store);// 更新db
        redisUtil.setStoreLocation(id,longitude,latitude);// 更新redis
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getNearbyStore(Double longitude, Double latitude, Integer radius) {
        List<NearbyStoreDTO> nearbyStores=redisUtil.getNearbyStores(longitude,latitude,radius,10);
        for(NearbyStoreDTO store:nearbyStores){
            Store s=storeMapper.selectById(store.getId());
            store.setId(s.getId());
            store.setName(s.getName());
            store.setStatus(s.getStatus());
            store.setProvince(s.getProvince());
            store.setCity(s.getCity());
            store.setDistrict(s.getDistrict());
            store.setAddress(s.getAddress());
            store.setOpenTime(s.getOpenTime());
        }
        return ApiResponse.success(JSONObject.of(
                "counts",nearbyStores.size(),
                "stores",nearbyStores));
    }

    @Override
    public ApiResponse getStoreLicenseById(Long id) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .selectAsClass(Store.class,StoreLicenseDTO.class)
                .eq(Store::getId,id);
        StoreLicenseDTO license=storeMapper.selectJoinOne(StoreLicenseDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("license",license));
    }
}
