package cn.dextea.customer.service.impl;

import cn.dextea.common.code.CustomerStatus;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.customer.CustomerModel;
import cn.dextea.common.util.DexteaJWTUtil;
import cn.dextea.customer.code.CustomerErrorCode;
import cn.dextea.customer.model.CustomerFilter;
import cn.dextea.customer.pojo.Customer;
import cn.dextea.customer.model.CustomerLoginRequest;
import cn.dextea.customer.mapper.CustomerMapper;
import cn.dextea.customer.service.CustomerService;
import cn.dextea.customer.util.AlipayUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class CustomerServiceImpl implements CustomerService {
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private AlipayUtil alipayUtil;
    @Resource
    private DexteaJWTUtil jwtUtil;
    @Override
    public DexteaApiResponse<CustomerModel> customerLogin(CustomerLoginRequest data) throws AlipayApiException {
        // 获取openId
        AlipaySystemOauthTokenResponse response=alipayUtil.getSystemOauthToken(data.getAuthCode());
        String openId = response.getOpenId();
        // 注册登录
        MPJLambdaWrapper<Customer> wrapper=new MPJLambdaWrapper<Customer>().eq(Customer::getOpenId,openId);
        Customer customer=customerMapper.selectJoinOne(wrapper);
        if (Objects.isNull(customer)){
            // 注册
            customer=Customer.builder()
                            .openId(openId)
                            .name("德贤茶友")
                            .status(CustomerStatus.ACTIVE.getValue())
                            .build();
            customerMapper.insert(customer);
        }
        // 判断状态
        if(customer.getStatus()!=CustomerStatus.ACTIVE.getValue()){
            return DexteaApiResponse.fail("登录失败",
                    CustomerErrorCode.CUSTOMER_FORBIDDEN.getCode(),CustomerErrorCode.CUSTOMER_FORBIDDEN.getMsg());
        }
        // 生成token
        String token= jwtUtil.createToken(customer.getId());
        return DexteaApiResponse.success(CustomerModel.builder()
                .id(customer.getId())
                .name(customer.getName())
                .token(token)
                .build());
    }

    @Override
    public DexteaApiResponse<IPage<CustomerModel>> getCustomerList(int current, int size, CustomerFilter filter) {
        MPJLambdaWrapper<Customer> wrapper=new MPJLambdaWrapper<Customer>()
                .selectAsClass(Customer.class, CustomerModel.class)
                .eqIfExists(Customer::getId,filter.getId())
                .eqIfExists(Customer::getOpenId,filter.getOpenId())
                .eqIfExists(Customer::getStatus,filter.getStatus());
        IPage<CustomerModel> page=customerMapper.selectJoinPage(
                new Page<>(current,size),
                CustomerModel.class,
                wrapper);
        if (current>page.getPages())
            page=customerMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    CustomerModel.class,
                    wrapper);
        return DexteaApiResponse.success(page);
    }

    @Override
    public DexteaApiResponse<CustomerModel> getCustomerDetail(Long id) {
        MPJLambdaWrapper<Customer> wrapper=new MPJLambdaWrapper<Customer>()
                .eq(Customer::getId,id)
                .selectAsClass(Customer.class,CustomerModel.class);
        CustomerModel customer=customerMapper.selectJoinOne(CustomerModel.class,wrapper);
        if(Objects.isNull(customer)){
            return DexteaApiResponse.notFound(CustomerErrorCode.CUSTOMER_NOT_FOUND.getCode(),
                    CustomerErrorCode.CUSTOMER_NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(customer);
    }

    @Override
    public DexteaApiResponse<Void> updateCustomerStatus(Long id, Integer status) {
        LambdaUpdateWrapper<Customer> wrapper=new LambdaUpdateWrapper<Customer>()
                .set(Customer::getStatus,status)
                .eq(Customer::getId,id);
        if(customerMapper.update(wrapper)==0){
            return DexteaApiResponse.notFound(CustomerErrorCode.CUSTOMER_NOT_FOUND.getCode(),
                    CustomerErrorCode.CUSTOMER_NOT_FOUND.getMsg());
        }else{
            return DexteaApiResponse.success();
        }
    }
}
