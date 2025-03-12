package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.service.CustomerService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class CustomerController {
    @Resource
    private CustomerService customerService;

    /**
     * 获取商品详情
     * @param id 商品ID
     * @param storeId 门店ID
     */
    @GetMapping("/product/{id:\\d+}")
    public ApiResponse getProductInfo(
            @PathVariable Long id,
            @RequestParam Long storeId) {
        return customerService.getProductInfo(id,storeId);
    }
}
