package cn.dextea.menu.service.impl;

import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.InternalService;
import jakarta.annotation.Resource;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
public class InternalServiceImpl implements InternalService {
    @Resource
    private MenuMapper menuMapper;
    @Override
    public boolean isMenuIdValid(Long id) {
        return Objects.nonNull(menuMapper.selectById(id));
    }
}
