package cn.dextea.store.service;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.CreateStoreDTO;
import cn.dextea.store.dto.SearchStoreDTO;
import cn.dextea.store.pojo.Store;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    ApiResponse getStoreList(int current, int size, SearchStoreDTO filter);
    ApiResponse create(CreateStoreDTO data);
    ApiResponse getStoreById(Long id);
}
