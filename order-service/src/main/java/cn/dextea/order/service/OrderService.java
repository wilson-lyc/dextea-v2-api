package cn.dextea.order.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.order.OrderModel;
import cn.dextea.order.model.OrderFilter;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface OrderService {
    DexteaApiResponse<IPage<OrderModel>> getOrderList(int current, int size, OrderFilter filter);
    DexteaApiResponse<OrderModel> getOrderBase(Long id);
    DexteaApiResponse<OrderModel> getOrderDetail(String id);
}
