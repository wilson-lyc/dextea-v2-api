package cn.dextea.menu.service;

import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.menu.MenuModel;
import cn.dextea.menu.model.menu.MenuBindResponse;
import cn.dextea.menu.model.menu.MenuCreateRequest;
import cn.dextea.menu.model.menu.MenuFilter;
import cn.dextea.menu.model.menu.MenuUpdateBaseRequest;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.javassist.NotFoundException;

import java.util.List;

/**
 * @author Lai Yongchao
 */
public interface MenuService {
    DexteaApiResponse<Void> createMenu(MenuCreateRequest data);
    DexteaApiResponse<IPage<MenuModel>> getMenuList(int current, int size, MenuFilter filter);
    DexteaApiResponse<MenuModel> getMenuDetail(Long id, Long storeId);
    DexteaApiResponse<MenuModel> getMenuBase(Long id);
    DexteaApiResponse<Void> updateMenuBase(Long id, MenuUpdateBaseRequest data);
    DexteaApiResponse<MenuBindResponse> storeBindMenu(Long id, List<Long> storeIds);
}
