package cn.dextea.store.service.impl;

import cn.dextea.store.mapper.StoreMapper;
import cn.dextea.common.pojo.Store;
import cn.dextea.store.service.InternalService;
import jakarta.annotation.Resource;
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
    public String getStoreName(Long id) throws IllegalArgumentException {
        Store store=storeMapper.selectById(id);
        if (Objects.isNull(store)){
            throw new IllegalArgumentException("门店不存在");
        }
        return store.getName();
    }
}
