package cn.dextea.store.controller;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.model.ImageModel;
import cn.dextea.common.model.SelectOptionModel;
import cn.dextea.common.model.store.StoreModel;
import cn.dextea.store.model.StoreCreateRequest;
import cn.dextea.store.model.StoreFilter;
import cn.dextea.store.model.StoreUpdateBaseRequest;
import cn.dextea.store.model.StoreUpdateLocationRequest;
import cn.dextea.store.service.StoreService;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.metadata.IPage;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class StoreController {
    @Resource
    StoreService storeService;

    /**
     * 创建门店
     * @param data 门店信息
     */
    @PostMapping("/store")
    public DexteaApiResponse<Void> createStore(@Valid @RequestBody StoreCreateRequest data) {
        return storeService.createStore(data);
    }

    /**
     * 获取门店列表
     * @param current 页码
     * @param size 分页大小
     * @param filter 过滤条件
     */
    @GetMapping("/store")
    public DexteaApiResponse<IPage<StoreModel>> getStoreList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid StoreFilter filter) {
        return storeService.getStoreList(current,size,filter);
    }

    /**
     * 获取门店选项
     */
    @GetMapping("/store/option")
    public DexteaApiResponse<List<SelectOptionModel>> getStoreOption() {
        return storeService.getStoreOption();
    }

    /**
     * 获取门店树选项
     */
    @GetMapping("/store/tree-option")
    public DexteaApiResponse<JSONArray> getStoreTreeOption(){
        return storeService.getStoreTreeOption();
    }

    /**
     * 获取门店基础信息
     * @param id 门店ID
     */
    @GetMapping("/store/{id:\\d+}/base")
    public DexteaApiResponse<StoreModel> getStoreBase(@PathVariable Long id) throws NotFoundException {
        return storeService.getStoreBase(id);
    }

    /**
     * 获取门店许可证
     * @param id 门店ID
     */
    @GetMapping("/store/{id:\\d+}/license")
    public DexteaApiResponse<List<ImageModel>> getStoreLicense(@PathVariable Long id) throws NotFoundException {
        return storeService.getStoreLicense(id);
    }

    /**
     * 获取门店状态
     * @param id 门店id
     */
    @GetMapping("/store/{id:\\d+}/status")
    public DexteaApiResponse<StoreModel> getStoreStatus(@PathVariable Long id) throws NotFoundException {
        return storeService.getStoreStatus(id);
    }

    /**
     * 获取门店位置
     * @param id 门店id
     */
    @GetMapping("/store/{id:\\d+}/location")
    public DexteaApiResponse<StoreModel> getStoreLocation(@PathVariable Long id) throws NotFoundException {
        return storeService.getStoreLocation(id);
    }

    /**
     * 更新门店基本信息
     * @param id 门店id
     * @param data 门店基础信息
     */
    @PutMapping("/store/{id:\\d+}/base")
    public DexteaApiResponse<Void> updateStoreBase(@PathVariable Long id, @RequestBody StoreUpdateBaseRequest data) throws NotFoundException {
        return storeService.updateStoreBase(id, data);
    }

    /**
     * 更新门店位置
     * @param id 门店id
     * @param data 经度,纬度
     */
    @PutMapping("/store/{id:\\d+}/location")
    public DexteaApiResponse<Void> updateStoreLocation(
            @PathVariable Long id,
            @RequestBody StoreUpdateLocationRequest data) throws NotFoundException {
        return storeService.updateStoreLocation(id, data);
    }

    /**
     * 更新门店状态
     * @param id 门店id
     * @param status 状态
     */
    @PutMapping("/store/{id:\\d+}/status")
    public DexteaApiResponse<Void> updateStoreStatus(
            @PathVariable Long id,
            @RequestParam Integer status) throws NotFoundException {
        return storeService.updateStoreStatus(id, status);
    }
}
