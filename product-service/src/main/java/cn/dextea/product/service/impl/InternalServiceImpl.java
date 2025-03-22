package cn.dextea.product.service.impl;

import cn.dextea.product.mapper.CategoryMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public boolean isProductIdValid(Long id) {
        return productMapper.selectById(id) != null;
    }

    @Override
    public boolean isCategoryIdValid(Long id) {
        return categoryMapper.selectById(id)!=null;
    }
}
