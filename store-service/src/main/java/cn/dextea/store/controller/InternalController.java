package cn.dextea.store.controller;

import cn.dextea.store.service.InternalService;
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
    @GetMapping("/store/internal/isStoreIdValid")
    public boolean isStoreIdValid(@RequestParam Long id) {
        return internalService.isStoreIdValid(id);
    }
}
