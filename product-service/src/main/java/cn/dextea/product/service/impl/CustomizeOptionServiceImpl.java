package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateCustomizeOptionDTO;
import cn.dextea.product.dto.UpdateCustomizeOptionDTO;
import cn.dextea.product.mapper.CustomizeMapper;
import cn.dextea.product.mapper.CustomizeOptionMapper;
import cn.dextea.product.pojo.Customize;
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
    private CustomizeMapper customizeMapper;
    @Override
    public ApiResponse create(CreateCustomizeOptionDTO data) {
        // 校验ID对应的客制化项目是否存在
        Customize customize = customizeMapper.selectById(data.getCustomizeId());
        if (customize == null) {
            return ApiResponse.badRequest(String.format("绑定的客制化项目不存在，id=%d", data.getCustomizeId()));
        }
        // 添加客制化选项
        CustomizeOption customizeOption = data.toCustomizeOption();
        customizeOptionMapper.insert(customizeOption);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getCustomizeOptionList(Long customizeId) {
        MPJLambdaWrapper<CustomizeOption> wrapper = new MPJLambdaWrapper<CustomizeOption>()
                .selectAll(CustomizeOption.class)
                .innerJoin(Customize.class,Customize::getId,CustomizeOption::getCustomizeId)
                .eq(Customize::getId, customizeId);
        List<CustomizeOption> list = customizeOptionMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("optionList", list));
    }

    @Override
    public ApiResponse getCustomizeOptionById(Long id) {
        CustomizeOption customizeOption = customizeOptionMapper.selectById(id);
        if (customizeOption == null) {
            return ApiResponse.notFound(String.format("资源不存在，id=%d", id));
        }
        return ApiResponse.success(JSONObject.of("option", customizeOption));
    }

    @Override
    public ApiResponse updateCustomizeOption(Long id, @Valid UpdateCustomizeOptionDTO data) {
        CustomizeOption customizeOption=data.toCustomizeOption();
        customizeOption.setId(id);
        int num = customizeOptionMapper.updateById(customizeOption);
        if (num == 0) {
            return ApiResponse.notFound(String.format("资源不存在，id=%d", id));
        }
        return ApiResponse.success("更新成功");
    }
}
