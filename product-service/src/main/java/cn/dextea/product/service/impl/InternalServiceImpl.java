package cn.dextea.product.service.impl;

import cn.dextea.product.mapper.CategoryMapper;
import cn.dextea.product.mapper.ItemMapper;
import cn.dextea.product.mapper.OptionMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private ProductMapper productMapper;
    @Resource
    private CategoryMapper categoryMapper;
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private OptionMapper optionMapper;

    @Override
    public boolean isProductIdValid(Long id) {
        return Objects.nonNull(productMapper.selectById(id));
    }

    @Override
    public boolean isCategoryIdValid(Long id) {
        return categoryMapper.selectById(id)!=null;
    }

    @Override
    public boolean isCustomizeItemIdValid(Long id) {
        return itemMapper.selectById(id) != null;
    }

    @Override
    public boolean isCustomizeOptionIdValid(Long id) {
        return optionMapper.selectById(id)!=null;
    }
}
