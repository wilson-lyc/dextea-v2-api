package cn.dextea.product.controller;

import cn.dextea.common.model.common.ApiResponse;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuProductModel;
import cn.dextea.common.model.product.ProductModel;
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
    @GetMapping("/product/customer/getProductDetail")
    public DexteaApiResponse<ProductModel> getProductInfo(
            @RequestParam Long productId,
            @RequestParam Long storeId) throws NotFoundException {
        return customerService.getProductInfo(productId,storeId);
    }
}
