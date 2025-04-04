package cn.dextea.customer.service.impl;

import cn.dextea.common.pojo.Customer;
import cn.dextea.customer.mapper.CustomerMapper;
import cn.dextea.customer.service.InternalService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private CustomerMapper customerMapper;

    @Override
    public String getCustomerOpenId(Long id) {
        MPJLambdaWrapper<Customer> wrapper=new MPJLambdaWrapper<Customer>()
                .select(Customer::getOpenId)
                .eq(Customer::getId,id);
        return customerMapper.selectJoinOne(String.class,wrapper);
    }
}
