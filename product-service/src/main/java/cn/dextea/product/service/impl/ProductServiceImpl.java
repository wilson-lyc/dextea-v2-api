package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.dto.CreateProductDTO;
import cn.dextea.product.dto.ProductDTO;
import cn.dextea.product.dto.SearchProductDTO;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.pojo.ProductType;
import cn.dextea.product.service.ProductService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductMapper productMapper;
    @Override
    public ApiResponse create(CreateProductDTO data) {
        Product product = data.toProduct();
        productMapper.insert(product);
        return ApiResponse.success();
    }

    @Override
    public ApiResponse getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if(product == null) {
            String msg = String.format("商品不存在，id=%d", id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success(JSONObject.of("product", product));
    }

    @Override
    public ApiResponse getProductList(int current, int size, SearchProductDTO filter) {
        // 联表查询
        MPJLambdaWrapper<Product> wrapper = new MPJLambdaWrapper<Product>()
                .selectAll(Product.class)
                .selectAs(ProductType::getName, ProductDTO::getTypeName)
                .innerJoin(ProductType.class, ProductType::getId, Product::getTypeId)
                .orderByAsc(Product::getId);
        // 添加过滤条件
        if (filter.getId() != null) {
            wrapper.eq("id", filter.getId());
        }
        if (filter.getName() != null && !filter.getName().isBlank()) {
            wrapper.like("name", filter.getName());
        }
        if (filter.getTypeId() != null) {
            wrapper.eq("type_id", filter.getTypeId());
        }
        if (filter.getStatus() != null) {
            wrapper.eq("status", filter.getStatus());
        }
        // 分页查询
        IPage<ProductDTO> page=productMapper.selectJoinPage(new Page<>(current, size), ProductDTO.class,wrapper);
        // 如果当前页码大于总页数，返回最后一页
        if (page.getCurrent() > page.getPages()) {
            page = productMapper.selectJoinPage(new Page<>(page.getPages(), size), ProductDTO.class,wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }
}
