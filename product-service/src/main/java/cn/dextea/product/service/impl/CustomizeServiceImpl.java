package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateCustomizeDTO;
import cn.dextea.product.dto.UpdateCustomizeDTO;
import cn.dextea.product.mapper.CustomizeMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.Customize;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.service.CustomizeService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.query.MPJQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomizeServiceImpl implements CustomizeService {
    @Resource
    private CustomizeMapper customizeMapper;
    @Resource
    private ProductMapper productMapper;

    @Override
    public ApiResponse create(CreateCustomizeDTO data) {
        Customize customize=data.toCustomize();
        // 校验ProduceId是否存在
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .eq(Product::getId,data.getProductId());
        Long num=productMapper.selectCount(wrapper);
        if(num==0){
            return ApiResponse.notFound(String.format("商品不存在，id=%d",data.getProductId()));
        }
        // 创建新的客制化项目
        customizeMapper.insert(customize);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getCustomizeList(Long productId) {
        MPJLambdaWrapper<Customize> wrapper=new MPJLambdaWrapper<Customize>()
                .selectAll(Customize.class)
                .innerJoin(Product.class,Product::getId,Customize::getProductId)
                .eq(Product::getId,productId);
        List<Customize> list=customizeMapper.selectList(wrapper);
        return ApiResponse.success(JSONObject.of("customizeList",list));
    }

    @Override
    public ApiResponse getCustomizeById(Long id) {
        MPJLambdaWrapper<Customize> wrapper=new MPJLambdaWrapper<Customize>()
                .selectAll(Customize.class)
                .eq(Customize::getId,id);
        Customize customize=customizeMapper.selectOne(wrapper);
        if(customize==null){
            return ApiResponse.notFound(String.format("资源不存在，id=%d",id));
        }
        return ApiResponse.success(JSONObject.of("customize",customize));
    }

    @Override
    public ApiResponse update(Long id, UpdateCustomizeDTO data) {
        Customize customize=data.toCustomize();
        customize.setId(id);
        int num=customizeMapper.updateById(customize);
        if(num==0){
            return ApiResponse.notFound(String.format("资源不存在，id=%d",id));
        }
        return ApiResponse.success("更新成功");
    }
}
