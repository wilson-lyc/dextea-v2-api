package cn.dextea.menu.service;

import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuModel;
import org.apache.ibatis.javassist.NotFoundException;

/**
 * @author Lai Yongchao
 */
public interface CustomerService {
    DexteaApiResponse<MenuModel> getStoreMenu(Long id) throws NotFoundException;
}
