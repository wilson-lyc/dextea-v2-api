package cn.dextea.store.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.ImageModel;
import cn.dextea.common.model.common.SelectOptionModel;
import cn.dextea.common.model.store.StoreModel;
import cn.dextea.store.model.StoreCreateRequest;
import cn.dextea.store.model.StoreFilter;
import cn.dextea.store.model.StoreUpdateBaseRequest;
import cn.dextea.store.model.StoreUpdateLocationRequest;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface StoreService {
    // 创建
    DexteaApiResponse<Void> createStore(StoreCreateRequest data);
    // 列表
    DexteaApiResponse<IPage<StoreModel>> getStoreList(int current, int size, StoreFilter filter);
    DexteaApiResponse<List<SelectOptionModel>> getStoreOption();
    DexteaApiResponse<JSONArray> getStoreTreeOption();
    // 单项
    DexteaApiResponse<StoreModel> getStoreBase(Long id) throws NotFoundException;
    DexteaApiResponse<List<ImageModel>> getStoreLicense(Long id) throws NotFoundException;
    DexteaApiResponse<StoreModel> getStoreStatus(Long id) throws NotFoundException;
    DexteaApiResponse<StoreModel> getStoreLocation(Long id) throws NotFoundException;
    // 更新
    DexteaApiResponse<Void> updateStoreBase(Long id, StoreUpdateBaseRequest data) throws NotFoundException;
    DexteaApiResponse<Void> updateStoreLocation(Long id, StoreUpdateLocationRequest body) throws NotFoundException;
    DexteaApiResponse<Void> updateStoreStatus(Long id, Integer status) throws NotFoundException;
}
