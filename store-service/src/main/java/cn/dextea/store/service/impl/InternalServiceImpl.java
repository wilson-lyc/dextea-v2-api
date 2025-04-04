package cn.dextea.store.service.impl;

import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.common.pojo.Store;
import cn.dextea.store.service.InternalService;
import cn.dextea.store.util.RedisUtil;
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
    @Resource
    private RedisUtil redisUtil;
    @Override
    public boolean isStoreIdValid(Long id) {
        return Objects.nonNull(storeMapper.selectById(id));
    }

    @Override
    public String getStoreName(Long id) throws IllegalArgumentException {
        Store store=storeMapper.selectById(id);
        if (Objects.isNull(store)){
            throw new IllegalArgumentException("门店不存在");
        }
        return store.getName();
    }

    @Override
    public boolean storeBindMenu(Long storeId, Long menuId) throws NotFoundException {
        Store store = Store.builder()
                .id(storeId)
                .menuId(menuId)
                .build();
        return storeMapper.updateById(store) >0;
    }

    @Override
    public Long getStoreMenuId(Long id) {
        Store store=storeMapper.selectById(id);
        return store.getMenuId();
    }

    @Override
    public Double getStoreDistance(Long storeId, Double longitude, Double latitude) {
        return redisUtil.getDistanceToStore(storeId,longitude,latitude);
    }
}
