package cn.dextea.menu.service.impl;

import cn.dextea.common.pojo.Menu;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private MenuMapper menuMapper;
    @Override
    public Menu getMenuById(Long id){
        return menuMapper.selectById(id);
    }
}
