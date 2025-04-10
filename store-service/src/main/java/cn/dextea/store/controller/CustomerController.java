package cn.dextea.store.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.store.StoreModel;
import cn.dextea.store.service.CustomerService;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@RestController
public class CustomerController {
    @Resource
    private CustomerService customerService;

    /**
     * 获取附近门店
     * @param longitude 经度
     * @param latitude 纬度
     * @param radius 距离
     * @param limit 数量
     */
    @GetMapping("/store/customer/getNearbyStore")
    public DexteaApiResponse<List<StoreModel>> getNearbyStore(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam Integer radius,
            @RequestParam Integer limit) {
        return customerService.getNearbyStore(longitude, latitude, radius,limit);
    }

    @GetMapping("/store/customer/searchStore")
    public DexteaApiResponse<List<StoreModel>> searchStore(@RequestParam String name){
        return customerService.searchStore(name);
    }

    /**
     * 获取门店详情
     * 携带经纬度则额外返回距离
     * @param id 门店ID
     * @param longitude 经度
     * @param latitude 纬度
     */
    @GetMapping("/store/customer/getStoreDetail")
    public DexteaApiResponse<StoreModel> getStoreDetail(
            @RequestParam Long id,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude) throws NotFoundException {
        return customerService.getStoreDetail(id, longitude, latitude);
    }
}
