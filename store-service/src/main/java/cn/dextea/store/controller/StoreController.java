package cn.dextea.store.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.dto.CreateStoreDTO;
import cn.dextea.store.dto.SearchStoreDTO;
import cn.dextea.store.dto.UpdateStoreDTO;
import cn.dextea.store.service.StoreService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/store")
public class StoreController {
    @Autowired
    StoreService storeService;

    /**
     * 创建门店
     * @param data {name,province,city,district,address,linkman,phone,openTime}
     */
    @PostMapping("")
    public ApiResponse create(@Valid @RequestBody CreateStoreDTO data) {
        return storeService.create(data);
    }

    /**
     * 获取门店详情
     * @param id 门店id
     */
    @GetMapping("/{id:\\d+}")
    public ApiResponse getStoreById(@PathVariable Long id) {
        return storeService.getStoreById(id);
    }

    /**
     * 获取门店列表
     * @param current 页码
     * @param size 分页大小
     */
    @PostMapping("/search")
    public ApiResponse getStoreList(
            @Valid @Min(value = 1,message = "current不能小于1") @RequestParam(defaultValue = "1") int current,
            @Valid @Min(value = 1,message = "size不能小于1") @RequestParam(defaultValue = "10") int size,
            @Valid @RequestBody SearchStoreDTO filter) {
        return storeService.getStoreList(current, size,filter);
    }

    /**
     * 更新门店运营状态
     * @param id 门店id
     * @param status 运营状态
     */
    @PutMapping("/status")
    public ApiResponse updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        return storeService.updateStatus(id, status);
    }

    /**
     * 更新门店基本信息
     * @param id 门店id
     * @param data {name,province,city,district,address,linkman,phone,openTime}
     */
    @PutMapping("/{id:\\d+}/base")
    public ApiResponse update(@PathVariable Long id, @RequestBody UpdateStoreDTO data) {
        return storeService.update(id, data);
    }

    /**
     * 上传营业执照
     * @param id 门店id
     * @param file 营业执照文件
     */
    @PostMapping(value = "/license/business", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadBusinessLicense(@RequestParam Long id, @RequestPart MultipartFile file) {
        return storeService.uploadBusinessLicense(id, file);
    }

    /**
     * 上传食品经营许可证
     * @param id 门店id
     * @param file 文件
     */
    @PostMapping(value = "/license/food", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse> uploadFoodLicense(@RequestParam Long id, @RequestPart MultipartFile file) {
        return storeService.uploadFoodLicense(id, file);
    }

    /**
     * 获取许可证
     * @param id 门店id
     */
    @GetMapping("/{id:\\d+}/license")
    public ApiResponse getLicense(@PathVariable Long id) {
        return storeService.getLicenseById(id);
    }
}
