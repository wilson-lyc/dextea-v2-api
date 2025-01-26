package cn.dextea.store.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.CreateStoreDTO;
import cn.dextea.store.dto.SearchStoreDTO;
import cn.dextea.store.dto.UpdateStoreDTO;
import cn.dextea.store.feign.TosFeign;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.pojo.StoreStatus;
import cn.dextea.store.service.StoreService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.protobuf.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public ApiResponse create(CreateStoreDTO data) {
        Store store=data.toStore();
        store.setStatus(StoreStatus.NOT_ACTIVE.getCode());// 新注册门店状态为未激活
        storeMapper.insert(store);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getStoreById(Long id) {
        QueryWrapper<Store> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        Store store=storeMapper.selectOne(wrapper);
        if (store==null){
            String mgs=String.format("门店不存在，ID=%d",id);
            return ApiResponse.notFound(mgs);
        }
        return ApiResponse.success(JSONObject.of("store",store));
    }

    @Override
    public ApiResponse getStoreList(int current, int size,SearchStoreDTO filter) {
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
        Store store=Store.builder()
                .status(status)
                .build();
        int num=storeMapper.update(store,new QueryWrapper<Store>().eq("id",id));
        if (num==0){
            String msg=String.format("门店不存在，ID=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse update(Long id, UpdateStoreDTO data) {
        Store store=data.toStore();
        store.setId(id);
        int num=storeMapper.updateById(store);
        if (num==0){
            String msg=String.format("门店不存在，ID=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success();
    }

    @Override
    public ResponseEntity<ApiResponse> uploadBusinessLicense(Long id, MultipartFile file) {
        // 查询旧的营业执照URL
        QueryWrapper<Store> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        String oldUrl=storeMapper.selectOne(wrapper).getBusinessLicense();
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
        QueryWrapper<Store> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        String oldUrl=storeMapper.selectOne(wrapper).getFoodBusinessLicense();
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
    public ApiResponse getLicenseById(Long id) {
        QueryWrapper<Store> wrapper=new QueryWrapper<>();
        wrapper.select("business_license","food_business_license");
        wrapper.eq("id",id);
        Store store=storeMapper.selectOne(wrapper);
        if (store==null){
            String msg=String.format("门店不存在，ID=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success(JSONObject.of(
                "business_license",store.getBusinessLicense(),
                "food_business_license",store.getFoodBusinessLicense()));
    }
}
