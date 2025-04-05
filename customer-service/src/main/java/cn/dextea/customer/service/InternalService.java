package cn.dextea.customer.service;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    String getCustomerOpenId(Long id);
    boolean verifyCustomerToken(String token);
}
