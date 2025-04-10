package cn.dextea.store.service.impl;

import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.store.pojo.Store;
import cn.dextea.store.service.InternalService;
import cn.dextea.store.util.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private StoreMapper storeMapper;

    @Override
    public boolean isStoreIdValid(Long id) {
        return Objects.nonNull(storeMapper.selectById(id));
    }

    @Override
    public String getStoreName(Long id){
        Store store=storeMapper.selectById(id);
        return Objects.isNull(store)?null:store.getName();
    }

    @Override
    public boolean storeBindMenu(Long storeId, Long menuId){
        LambdaUpdateWrapper<Store> wrapper=new LambdaUpdateWrapper<Store>()
                .eq(Store::getId,storeId)
                .set(Store::getMenuId,menuId);
        return storeMapper.update(wrapper)>0;
    }

    @Override
    public Long getStoreMenuId(Long id) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .select(Store::getMenuId)
                .eq(Store::getId,id);
        return storeMapper.selectJoinOne(Long.class,wrapper);
    }

    @Override
    public String getStorePhone(Long id) {
        MPJLambdaWrapper<Store> wrapper=new MPJLambdaWrapper<Store>()
                .select(Store::getPhone)
                .eq(Store::getId,id);
        return storeMapper.selectJoinOne(String.class,wrapper);
    }
}
