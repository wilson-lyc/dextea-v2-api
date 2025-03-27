package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.service.CustomerService;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
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
     * @param productId 商品ID
     * @param storeId 门店ID
     */
    @GetMapping("/product/customer/getProductInfo")
    public ApiResponse getProductInfo(
            @RequestParam Long productId,
            @RequestParam Long storeId) throws NotFoundException {
        return customerService.getProductInfo(productId,storeId);
    }
}
