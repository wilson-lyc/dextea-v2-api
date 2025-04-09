package cn.dextea.common.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CounterOrderListModel {
    private List<OrderModel> making;
    private List<OrderModel> waitPick;
    private List<OrderModel> done;
}
