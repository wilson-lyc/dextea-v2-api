package cn.dextea.store.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.store.service.CustomerService;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.*;

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
    @GetMapping("/store/customer/nearby")
    public ApiResponse getNearbyStore(
            @RequestParam Double longitude,
            @RequestParam Double latitude,
            @RequestParam Integer radius,
            @RequestParam Integer limit) {
        return customerService.getNearbyStore(longitude, latitude, radius,limit);
    }

    /**
     * 获取门店信息
     * 携带经纬度则额外返回距离
     * @param id 门店id
     * @param longitude 经度
     * @param latitude 纬度
     */
    @GetMapping("/store/customer/{id:\\d+}/base")
    public ApiResponse getStoreInfo(
            @PathVariable Long id,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double latitude) throws NotFoundException {
        return customerService.getStoreInfo(id, longitude, latitude);
    }
}
