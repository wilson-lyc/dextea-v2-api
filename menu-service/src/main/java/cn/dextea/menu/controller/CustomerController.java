package cn.dextea.menu.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.menu.service.CustomerService;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
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
     * 获取门店的菜单
     * @param id 门店ID
     */
    @GetMapping("/store/{id:\\d+}/menu")
    public ApiResponse getStoreMenu(@PathVariable Long id) throws NotFoundException {
        return customerService.getStoreMenu(id);
    }
}
