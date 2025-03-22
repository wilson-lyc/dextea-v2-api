package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CustomizeOptionCreateDTO;
import cn.dextea.product.dto.CustomizeOptionUpdateDTO;
import cn.dextea.product.dto.OptionListDTO;
import cn.dextea.product.mapper.CustomizeItemMapper;
import cn.dextea.product.mapper.CustomizeOptionMapper;
import cn.dextea.product.pojo.CustomizeItem;
import cn.dextea.product.pojo.CustomizeOption;
import cn.dextea.product.service.CustomizeOptionService;
import com.alibaba.fastjson2.JSONObject;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomizeOptionServiceImpl implements CustomizeOptionService {
    @Resource
    private CustomizeOptionMapper customizeOptionMapper;
    @Resource
    private CustomizeItemMapper customizeItemMapper;
    @Override
    public ApiResponse createOption(Long id, CustomizeOptionCreateDTO data) {
        // 校验ID对应的客制化项目是否存在
        if (customizeItemMapper.selectById(id) == null) {
            return ApiResponse.badRequest(String.format("无法与ID=%d的客制化项目绑定", id));
        }
        // 添加客制化选项
        CustomizeOption option = data.toCustomizeOption();
        option.setItemId(id);// 绑定项目
        option.setStatus(0);// 默认为禁用
        customizeOptionMapper.insert(option);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getOptionList(Long itemId) {
        MPJLambdaWrapper<CustomizeOption> wrapper = new MPJLambdaWrapper<CustomizeOption>()
                .selectAsClass(CustomizeOption.class, OptionListDTO.class)
                .innerJoin(CustomizeItem.class, CustomizeItem::getId,CustomizeOption::getItemId)
                .orderByAsc(CustomizeOption::getSort)
                .eq(CustomizeItem::getId, itemId);
        List<OptionListDTO> list = customizeOptionMapper.selectJoinList(OptionListDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("options", list));
    }

    @Override
    public ApiResponse getById(Long id) {
        CustomizeOption customizeOption = customizeOptionMapper.selectById(id);
        if (customizeOption == null) {
            return ApiResponse.notFound(String.format("资源不存在，id=%d", id));
        }
        return ApiResponse.success(JSONObject.of("option", customizeOption));
    }

    @Override
    public ApiResponse update(Long id, @Valid CustomizeOptionUpdateDTO data) {
        CustomizeOption customizeOption=data.toCustomizeOption();
        customizeOption.setId(id);
        int num = customizeOptionMapper.updateById(customizeOption);
        if (num == 0) {
            return ApiResponse.notFound(String.format("资源不存在，id=%d", id));
        }
        return ApiResponse.success("更新成功");
    }
}
