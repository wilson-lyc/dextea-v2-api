package cn.dextea.product.controller;

import cn.dextea.product.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Lai Yongchao
 */
@RestController
public class InternalController {
    @Resource
    private InternalService internalService;
    @GetMapping("/product/internal/isProductIdValid")
    public boolean isProductIdValid(@RequestParam Long id) {
        return internalService.isProductIdValid(id);
    }

    @GetMapping("/product/internal/isCategoryIdValid")
    public boolean isCategoryIdValid(@RequestParam Long id) {
        return internalService.isCategoryIdValid(id);
    }

    @GetMapping("/product/internal/isCustomizeItemIdValid")
    public boolean isCustomizeItemIdValid(@RequestParam Long id) {
        return internalService.isCustomizeItemIdValid(id);
    }

    @GetMapping("/product/internal/isCustomizeOptionIdValid")
    public boolean isCustomizeOptionIdValid(@RequestParam Long id) {
        return internalService.isCustomizeOptionIdValid(id);
    }
}
