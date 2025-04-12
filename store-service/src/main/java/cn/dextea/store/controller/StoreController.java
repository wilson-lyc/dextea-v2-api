package cn.dextea.store.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.common.ImageModel;
import cn.dextea.common.model.common.SelectOptionModel;
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
    @SaCheckPermission("store:store:create")
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
    @SaCheckPermission("store:store:read")
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
    @SaCheckPermission("store:store:read")
    public DexteaApiResponse<List<SelectOptionModel>> getStoreOption() {
        return storeService.getStoreOption();
    }

    /**
     * 获取门店树选项
     */
    @GetMapping("/store/tree-option")
    @SaCheckPermission("store:store:read")
    public DexteaApiResponse<JSONArray> getStoreTreeOption(){
        return storeService.getStoreTreeOption();
    }

    /**
     * 获取门店基础信息
     * @param id 门店ID
     */
    @GetMapping("/store/{id:\\d+}/base")
    @SaCheckPermission("store:store:read")
    public DexteaApiResponse<StoreModel> getStoreBase(@PathVariable Long id){
        return storeService.getStoreBase(id);
    }

    /**
     * 获取门店许可证
     * @param id 门店ID
     */
    @GetMapping("/store/{id:\\d+}/license")
    @SaCheckPermission("store:store:read")
    public DexteaApiResponse<List<ImageModel>> getStoreLicense(@PathVariable Long id){
        return storeService.getStoreLicense(id);
    }

    /**
     * 获取门店状态
     * @param id 门店id
     */
    @GetMapping("/store/{id:\\d+}/status")
    @SaCheckPermission("store:store:read")
    public DexteaApiResponse<StoreModel> getStoreStatus(@PathVariable Long id){
        return storeService.getStoreStatus(id);
    }

    /**
     * 获取门店位置
     * @param id 门店id
     */
    @GetMapping("/store/{id:\\d+}/location")
    @SaCheckPermission("store:store:read")
    public DexteaApiResponse<StoreModel> getStoreLocation(@PathVariable Long id){
        return storeService.getStoreLocation(id);
    }

    /**
     * 更新门店基本信息
     * @param id 门店id
     * @param data 门店基础信息
     */
    @PutMapping("/store/{id:\\d+}/base")
    @SaCheckPermission("store:store:update:base")
    public DexteaApiResponse<Void> updateStoreBase(@PathVariable Long id, @RequestBody StoreUpdateBaseRequest data){
        return storeService.updateStoreBase(id, data);
    }

    /**
     * 更新门店位置
     * @param id 门店id
     * @param data 经度,纬度
     */
    @PutMapping("/store/{id:\\d+}/location")
    @SaCheckPermission("store:store:update:location")
    public DexteaApiResponse<Void> updateStoreLocation(
            @PathVariable Long id,
            @RequestBody StoreUpdateLocationRequest data){
        return storeService.updateStoreLocation(id, data);
    }

    /**
     * 更新门店状态
     * @param id 门店id
     * @param status 状态
     */
    @PutMapping("/store/{id:\\d+}/status")
    @SaCheckPermission("store:store:update:status")
    public DexteaApiResponse<Void> updateStoreStatus(
            @PathVariable Long id,
            @RequestParam Integer status){
        return storeService.updateStoreStatus(id, status);
    }
}
