package cn.dextea.customer.service.impl;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.pojo.Customer;
import cn.dextea.customer.dto.CustomerListResponse;
import cn.dextea.customer.dto.CustomerLoginRequest;
import cn.dextea.customer.dto.CustomerLoginResponse;
import cn.dextea.customer.mapper.CustomerMapper;
import cn.dextea.customer.service.CustomerService;
import cn.dextea.customer.util.AlipayUtil;
import cn.dextea.customer.util.JWTUtil;
import com.alipay.api.AlipayApiException;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
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
    private JWTUtil jwtUtil;
    @Override
    public DexteaApiResponse<CustomerLoginResponse> customerLogin(CustomerLoginRequest data) throws AlipayApiException {
        // 获取openId
        AlipaySystemOauthTokenResponse response=alipayUtil.getSystemOauthToken(data.getAuthCode());
        String openId= response.getOpenId();
        // 注册登录
        MPJLambdaWrapper<Customer> wrapper=new MPJLambdaWrapper<Customer>().eq(Customer::getOpenId,openId);
        Customer customer=customerMapper.selectJoinOne(wrapper);
        if (Objects.isNull(customer)){
            // 注册
            customer=Customer.builder()
                            .openId(openId)
                            .name("德贤茶友")
                            .build();
            customerMapper.insert(customer);
        }
        // 生成token
        String token= jwtUtil.createToken(customer.getId());
        return DexteaApiResponse.success(CustomerLoginResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .token(token)
                .build());
    }

    @Override
    public DexteaApiResponse<IPage<CustomerListResponse>> getCustomerList(int current, int size) {
        MPJLambdaWrapper<Customer> warpper=new MPJLambdaWrapper<Customer>()
                .selectAsClass(Customer.class, CustomerListResponse.class);
        IPage<CustomerListResponse> page=customerMapper.selectJoinPage(
                new Page<>(current,size),
                CustomerListResponse.class,
                warpper);
        if (current>page.getPages())
            page=customerMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    CustomerListResponse.class,
                    warpper);
        return DexteaApiResponse.success(page);
    }
}
