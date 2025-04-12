package cn.dextea.menu.service;

import cn.dextea.common.model.menu.MenuModel;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    MenuModel getMenuDetail(Long id, String mode, Long storeId);

    boolean isMenuIdValid(Long id);
}
