package cn.dextea.product.controller;

import cn.dextea.product.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;
    @GetMapping("/product/internal/{id:\\d+}/valid")
    public boolean isProductIdValid(@PathVariable Long id) {
        return internalService.isProductIdValid(id);
    }
}
