package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeItemCreateDTO;
import cn.dextea.product.dto.CustomizeItemUpdateDTO;
import cn.dextea.product.service.CustomizeItemService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
@RequestMapping("/customize/item")
public class CustomizeItemController {

    @Resource
    private CustomizeItemService customizeItemService;

    @PostMapping
    public ApiResponse create(@Valid @RequestBody CustomizeItemCreateDTO data){
        return customizeItemService.create(data);
    }

    @GetMapping
    public ApiResponse getList(@RequestParam("productId") Long productId){
        return customizeItemService.getList(productId);
    }

    @GetMapping("/{id:\\d+}")
    public ApiResponse getById(@PathVariable Long id){
        return customizeItemService.getById(id);
    }

    @PutMapping("/{id:\\d+}")
    public ApiResponse update(@PathVariable Long id, @Valid @RequestBody CustomizeItemUpdateDTO data){
        return customizeItemService.update(id, data);
    }
}
