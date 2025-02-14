package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateCustomizeOptionDTO;
import cn.dextea.product.dto.UpdateCustomizeOptionDTO;
import cn.dextea.product.service.CustomizeOptionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/customizeOption")
public class CustomizeOptionController {
    @Resource
    private CustomizeOptionService customizeOptionService;

    @PostMapping
    public ApiResponse create(@Valid @RequestBody CreateCustomizeOptionDTO data) {
        return customizeOptionService.create(data);
    }

    @GetMapping
    public ApiResponse getCustomizeOptionList(@RequestParam("customizeId") Long customizeId) {
        return customizeOptionService.getCustomizeOptionList(customizeId);
    }

    @GetMapping("{id:\\d+}")
    public ApiResponse getCustomizeOptionById(@PathVariable Long id) {
        return customizeOptionService.getCustomizeOptionById(id);
    }

    @PutMapping("{id:\\d+}")
    public ApiResponse updateCustomizeOption(@PathVariable Long id, @Valid @RequestBody UpdateCustomizeOptionDTO data) {
        return customizeOptionService.updateCustomizeOption(id, data);
    }
}
