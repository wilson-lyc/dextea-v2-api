package cn.dextea.product.controller;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeOptionCreateDTO;
import cn.dextea.product.dto.CustomizeOptionUpdateDTO;
import cn.dextea.product.service.CustomizeOptionService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * @author Lai Yongchao
 */
@RestController
public class CustomizeOptionController {
    @Resource
    private CustomizeOptionService customizeOptionService;

    @PostMapping("/product/customize/{id:\\d+}/option")
    public ApiResponse createOption(
            @PathVariable Long id,
            @Valid @RequestBody CustomizeOptionCreateDTO createDTO) {
        return customizeOptionService.createOption(id,createDTO);
    }

    @GetMapping("/product/customize/{itemId:\\d+}/option")
    public ApiResponse getOptionList(@PathVariable Long itemId) {
        return customizeOptionService.getOptionList(itemId);
    }

    @GetMapping("{id:\\d+}")
    public ApiResponse getById(@PathVariable Long id) {
        return customizeOptionService.getById(id);
    }

    @PutMapping("{id:\\d+}")
    public ApiResponse update(@PathVariable Long id, @Valid @RequestBody CustomizeOptionUpdateDTO data) {
        return customizeOptionService.update(id, data);
    }
}
