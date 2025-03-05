package cn.dextea.store.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.StoreCreateDTO;
import cn.dextea.store.dto.StoreFilter;
import cn.dextea.store.dto.StoreUpdateDTO;
import cn.dextea.store.service.StoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;

/**
 * @author Lai Yongchao
 */
@RestController
public class StoreController {
    @Autowired
    StoreService storeService;

    /**
     * 创建门店
     * @param data {name,province,city,district,address,linkman,phone,openTime}
     */
    @PostMapping("/store")
    public ApiResponse create(@Valid @RequestBody StoreCreateDTO data) {
        return storeService.createStore(data);
    }

    /**
     * 获取门店详情
     * @param id 门店id
     */
    @GetMapping("/store/{id:\\d+}")
    public ApiResponse getStoreById(@PathVariable Long id) {
        return storeService.getStoreById(id);
    }

    /**
     * 获取门店列表
     * @param current 页码
     * @param size 分页大小
     */
    @GetMapping("/store")
    public ApiResponse getStoreList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid StoreFilter filter) {
        return storeService.getStoreList(current, size,filter);
    }

    /**
     * 更新门店状态
     * @param id 门店id
     * @param status 状态
     */
    @PutMapping("/store/{id:\\d+}/status")
    public ApiResponse updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return storeService.updateStatus(id, status);
    }

    /**
     * 更新门店基本信息
     * @param id 门店id
     * @param data {name,province,city,district,address,linkman,phone,openTime}
     */
    @PutMapping("/store/{id:\\d+}/base")
    public ApiResponse updateBase(@PathVariable Long id, @RequestBody StoreUpdateDTO data) {
        return storeService.updateBase(id, data);
    }

    /**
     * 上传营业执照
     * @param id 门店id
     * @param file 营业执照文件
     */
    @PostMapping(value = "/store/license/business", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadBusinessLicense(@RequestParam Long id, @RequestPart MultipartFile file) {
        return storeService.uploadBusinessLicense(id, file);
    }

    /**
     * 上传食品经营许可证
     * @param id 门店id
     * @param file 文件
     */
    @PostMapping(value = "/store/license/food", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadFoodLicense(@RequestParam Long id, @RequestPart MultipartFile file) {
        return storeService.uploadFoodLicense(id, file);
    }

    /**
     * 获取门店下拉选项
     */
    @GetMapping("/store/selectOptions")
    public ApiResponse getSelectOptions() {
        return storeService.getSelectOptions();
    }

    /**
     * 更新门店位置
     * @param id 门店id
     * @param longitude 经度
     * @param latitude 纬度
     */
    @PutMapping("/store/{id:\\d+}/location")
    public ApiResponse updateLocation(
            @PathVariable Long id,
            @RequestParam BigDecimal longitude,
            @RequestParam BigDecimal latitude) {
        return storeService.updateLocation(id, longitude, latitude);
    }
}
