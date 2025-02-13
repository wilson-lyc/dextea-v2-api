package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateCustomizeDTO;
import cn.dextea.product.dto.UpdateCustomizeDTO;
import cn.dextea.product.service.CustomizeService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/customize")
public class CustomizeController {

    @Resource
    private CustomizeService customizeService;

    @PostMapping
    public ApiResponse create(@Valid @RequestBody CreateCustomizeDTO data){
        return customizeService.create(data);
    }

    @GetMapping
    public ApiResponse getCustomizeList(@RequestParam("productId") Long productId){
        return customizeService.getCustomizeList(productId);
    }

    @GetMapping("/{id:\\d+}")
    public ApiResponse getCustomizeById(@PathVariable Long id){
        return customizeService.getCustomizeById(id);
    }

    @PutMapping("/{id:\\d+}")
    public ApiResponse updateCustomize(@PathVariable Long id, @Valid @RequestBody UpdateCustomizeDTO data){
        return customizeService.update(id, data);
    }
}
