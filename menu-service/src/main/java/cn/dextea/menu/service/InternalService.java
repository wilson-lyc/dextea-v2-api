package cn.dextea.menu.service;

import cn.dextea.menu.pojo.Menu;

/**
 * @author Lai Yongchao
 */
public interface InternalService {
    Menu getMenuById(Long id);

    boolean isMenuIdValid(Long id);
}
