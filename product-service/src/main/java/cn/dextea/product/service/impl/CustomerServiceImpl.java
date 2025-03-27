package cn.dextea.product.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.ProductFeign;
import cn.dextea.common.pojo.CustomizeOptionStoreStatus;
import cn.dextea.product.dto.option.OptionListDTO;
import cn.dextea.product.mapper.ItemMapper;
import cn.dextea.product.mapper.OptionMapper;
import cn.dextea.product.mapper.ProductMapper;
import cn.dextea.common.pojo.CustomizeItem;
import cn.dextea.common.pojo.CustomizeOption;
import cn.dextea.common.pojo.Product;
import cn.dextea.product.service.CustomerService;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private ItemMapper itemMapper;
    @Resource
    private OptionMapper optionMapper;
    @Resource
    private ProductMapper productMapper;
    @Resource
    private ProductFeign productFeign;

    @Override
    public ApiResponse getProductInfo(Long productId, Long storeId) throws NotFoundException {
        Product product = productFeign.getProductById(productId,storeId);
        if (Objects.isNull(product))
            throw new NotFoundException("商品不存在");
        // 获取客制化项目
        MPJLambdaWrapper<CustomizeItem> itemWrapper=new MPJLambdaWrapper<CustomizeItem>()
                .selectAll(CustomizeItem.class)
                .eq(CustomizeItem::getProductId,productId)
                .orderByAsc(CustomizeItem::getSort);
        List<CustomizeItem> itemList=itemMapper.selectJoinList(itemWrapper);
        JSONArray customizeArray=new JSONArray();
        for (CustomizeItem item:itemList){
            JSONObject itemJSon=JSONObject.from(item);
            MPJLambdaWrapper<CustomizeOption> optionWrapper=new MPJLambdaWrapper<CustomizeOption>()
                    .selectAll(CustomizeOption.class)
                    .eq(CustomizeOption::getItemId,item.getId())
                    .leftJoin(CustomizeOptionStoreStatus.class,"os", on -> on
                            .eq(CustomizeOptionStoreStatus::getOptionId,CustomizeOption::getId)
                            .eq(CustomizeOptionStoreStatus::getStoreId,storeId))
                    .selectFunc("coalesce(%s,1)",arg ->arg
                                    .accept(CustomizeOptionStoreStatus::getStatus),
                            OptionListDTO::getStoreStatus)
                    .orderByAsc(CustomizeOption::getSort);
            List<OptionListDTO> options=optionMapper.selectJoinList(OptionListDTO.class,optionWrapper);
            itemJSon.put("options",options);
            customizeArray.add(itemJSon);
        }
        JSONObject productJSon=JSONObject.from(product);
        productJSon.put("customize",customizeArray);
        return ApiResponse.success(JSONObject.of("product",productJSon));
    }
}
