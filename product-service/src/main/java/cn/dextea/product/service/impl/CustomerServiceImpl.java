package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.product.mapper.CustomizeItemMapper;
import cn.dextea.product.mapper.CustomizeOptionMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.product.pojo.CustomizeItem;
import cn.dextea.product.pojo.CustomizeOption;
import cn.dextea.product.pojo.Product;
import cn.dextea.product.service.CustomerService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private CustomizeItemMapper customizeItemMapper;
    @Resource
    private CustomizeOptionMapper customizeOptionMapper;
    @Resource
    private ProductMapper productMapper;

    @Override
    public ApiResponse getProductInfo(Long id, Long storeId) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return ApiResponse.notFound(String.format("不存在id=%d的商品", id));
        }
        JSONObject productJson=JSONObject.from(product);
        JSONArray customize=new JSONArray();
        // 查询客制化项目
        QueryWrapper<CustomizeItem> itemWrapper=new QueryWrapper<>();
        itemWrapper.eq("product_id",id);
        itemWrapper.eq("status",1);
        itemWrapper.orderByAsc("sort");
        List<CustomizeItem> customizeItem=customizeItemMapper.selectList(itemWrapper);
        for(CustomizeItem item:customizeItem){
            JSONObject itemJson=JSONObject.from(item);
            // 查询客制化选项
            QueryWrapper<CustomizeOption> optionWrapper=new QueryWrapper<>();
            optionWrapper.eq("item_id",item.getId());
            optionWrapper.eq("status",1);
            optionWrapper.orderByAsc("sort");
            List<CustomizeOption> customizeOption=customizeOptionMapper.selectList(optionWrapper);
            itemJson.put("options",customizeOption);
            customize.add(itemJson);
        }
        productJson.put("customize",customize);
        return ApiResponse.success(JSONObject.of("product", productJson));
    }
}
