package cn.dextea.menu.service;

import cn.dextea.common.dto.ApiResponse;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    ApiResponse getStoreMenu(Long id) throws NotFoundException;
}
