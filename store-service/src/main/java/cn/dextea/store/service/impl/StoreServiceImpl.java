package cn.dextea.store.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.StoreCreateDTO;
import cn.dextea.store.dto.StoreFilter;
import cn.dextea.store.dto.StoreSelectOption;
import cn.dextea.store.dto.StoreUpdateDTO;
import cn.dextea.store.feign.TosFeign;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.service.StoreService;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    @Autowired
    StoreMapper storeMapper;
    @Autowired
    TosFeign tosFeign;

    @Override
    public ApiResponse createStore(StoreCreateDTO data) {
        Store store=data.toStore();
        store.setStatus(0);// 新注册门店状态为未激活
        // 获取初步定位
        JSONObject key=JSONObject.of("keyWord",store.getProvince()+store.getCity()+store.getDistrict()+store.getAddress());
        try{
            HttpResponse res= HttpRequest.get("https://api.tianditu.gov.cn/geocoder")
                    .form("ds",key.toJSONString())
                    .form("tk","7e3e6c02516464cd1c24ae21f562723c")
                    .execute();
            JSONObject location=JSONObject.parseObject(res.body()).getJSONObject("location");
            store.setLatitude(location.getBigDecimal("lat"));
            store.setLongitude(location.getBigDecimal("lon"));
        }catch (Exception e){
            log.error("获取门店初步定位失败",e);
        }
        storeMapper.insert(store);
        return ApiResponse.success("门店创建成功");
    }

    @Override
    public ApiResponse getStoreById(Long id) {
        Store store=storeMapper.selectById(id);
        if (store==null){
            return ApiResponse.notFound(String.format("不存在ID=%d的门店",id));
        }
        return ApiResponse.success(JSONObject.of("store",store));
    }

    @Override
    public ApiResponse getStoreList(int current, int size, StoreFilter filter) {
        QueryWrapper<Store> wrapper=new QueryWrapper<>();
        if(filter.getId()!=null){
            wrapper.eq("id",filter.getId());
        }
        if (filter.getName()!=null&&!filter.getName().isBlank()){
            wrapper.like("name",filter.getName());
        }
        if (filter.getStatus()!=null){
            wrapper.eq("status",filter.getStatus());
        }
        if (filter.getProvince()!=null&&!filter.getProvince().isBlank()){
            wrapper.eq("province",filter.getProvince());
        }
        if (filter.getCity()!=null&&!filter.getCity().isBlank()){
            wrapper.eq("city",filter.getCity());
        }
        if (filter.getDistrict()!=null&&!filter.getDistrict().isBlank()){
            wrapper.eq("district",filter.getDistrict());
        }
        if (filter.getLinkman()!=null&&!filter.getLinkman().isBlank()){
            wrapper.eq("linkman",filter.getLinkman());
        }
        if (filter.getPhone()!=null&&!filter.getPhone().isBlank()) {
            wrapper.eq("phone", filter.getPhone());
        }
        Page<Store> page = new Page<>(current, size);
        page=storeMapper.selectPage(page,wrapper);
        // 如果当前页码大于总页数，返回最后一页
        if (page.getCurrent()>page.getPages()){
            page.setCurrent(page.getPages());
            page=storeMapper.selectPage(page,wrapper);
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
        try{
            tosFeign.delete(oldUrl);
        }catch (Exception e){
            log.error("删除旧营业执照失败",e);
        }
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
        try{
            tosFeign.delete(oldUrl);
        }catch (Exception e){
            log.error("删除旧食品经营许可证失败",e);
        }
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
    public ApiResponse updateLocation(Long id, BigDecimal longitude, BigDecimal latitude) {
        Store store=Store.builder()
                .id(id)
                .longitude(longitude)
                .latitude(latitude)
                .build();
        storeMapper.updateById(store);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getNearbyStore(BigDecimal longitude, BigDecimal latitude, Integer distance) {
        List<Store> stores=storeMapper.selectList(null);
        return ApiResponse.success(JSONObject.of("stores",stores));// temp
    }
}
