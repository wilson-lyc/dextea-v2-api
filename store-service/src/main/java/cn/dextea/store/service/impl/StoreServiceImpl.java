package cn.dextea.store.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.CreateStoreDTO;
import cn.dextea.store.dto.SearchStoreDTO;
import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.pojo.StoreStatus;
import cn.dextea.store.service.StoreService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class StoreServiceImpl implements StoreService {
    @Autowired
    StoreMapper storeMapper;

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
}
