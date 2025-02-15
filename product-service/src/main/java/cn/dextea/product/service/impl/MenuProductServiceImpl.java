package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.MenuProductCreateDTO;
import cn.dextea.product.dto.MenuProductDTO;
import cn.dextea.product.dto.MenuProductUpdateDTO;
import cn.dextea.product.mapper.MenuProductMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.MenuProduct;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.service.MenuProductService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class MenuProductServiceImpl implements MenuProductService {
    @Resource
    private MenuProductMapper menuProductMapper;
    @Resource
    private ProductMapper productMapper;

    @Override
    public ApiResponse createMenuProduct(MenuProductCreateDTO data) {
        // 检查是否已经绑定
        QueryWrapper<MenuProduct> wrapper=new QueryWrapper<>();
        wrapper.eq("type_id",data.getTypeId());
        wrapper.eq("product_id",data.getProductId());
        if(menuProductMapper.selectCount(wrapper)>0){
            return ApiResponse.badRequest("已经绑定");
        }
        // 绑定商品
        MenuProduct menuProduct=data.toMenuProduct();
        menuProductMapper.insert(menuProduct);
        return ApiResponse.success("创建成功");
    }

    @Override
    public ApiResponse getMenuProductListByTypeId(Long typeId) {
        MPJLambdaWrapper<Product> wrapper=new MPJLambdaWrapper<Product>()
                .select(Product::getId,Product::getName,Product::getPrice)
                .select(MenuProduct::getSort)
                .innerJoin(MenuProduct.class, MenuProduct::getProductId,Product::getId)
                .orderByAsc(MenuProduct::getSort)
                .eq(MenuProduct::getTypeId,typeId);
        List<MenuProductDTO> list=productMapper.selectJoinList(MenuProductDTO.class,wrapper);
        return ApiResponse.success(JSONObject.of("products",list));
    }

    @Override
    public ApiResponse unbindProduct(Long typeId, Long productId) {
        QueryWrapper<MenuProduct> wrapper=new QueryWrapper<>();
        wrapper.eq("type_id",typeId);
        wrapper.eq("product_id",productId);
        menuProductMapper.delete(wrapper);
        return ApiResponse.success("解绑成功");
    }

    @Override
    public ApiResponse getMenuProductBase(Long typeId, Long productId) {
        QueryWrapper<MenuProduct> wrapper=new QueryWrapper<>();
        wrapper.eq("type_id",typeId);
        wrapper.eq("product_id",productId);
        MenuProduct menuProduct=menuProductMapper.selectOne(wrapper);
        if(menuProduct==null){
            return ApiResponse.badRequest("未绑定");
        }
        return ApiResponse.success(JSONObject.of("product",menuProduct));
    }

    @Override
    public ApiResponse updateMenuProductBase(Long typeId, Long productId, MenuProductUpdateDTO data) {
        UpdateWrapper<MenuProduct> wrapper=new UpdateWrapper<>();
        wrapper.eq("type_id",typeId);
        wrapper.eq("product_id",productId);
        wrapper.set("sort",data.getSort());
        menuProductMapper.update(wrapper);
        return ApiResponse.success("更新成功");

    }
}
