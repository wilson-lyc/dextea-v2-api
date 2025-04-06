package cn.dextea.menu.controller;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuModel;
import cn.dextea.menu.service.CustomerService;
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
     * 获取门店的菜单
     * @param id 门店ID
     */
    @GetMapping("/menu/customer/getStoreMenu")
    public DexteaApiResponse<MenuModel> getStoreMenu(@RequestParam Long id) throws NotFoundException {
        return customerService.getStoreMenu(id);
    }
}
