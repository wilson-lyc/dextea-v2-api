package cn.dextea.menu.service.impl;

import cn.dextea.menu.mapper.MenuGroupMapper;
import cn.dextea.menu.mapper.MenuMapper;
import cn.dextea.menu.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private MenuMapper menuMapper;
    @Resource
    private MenuGroupMapper menuGroupMapper;
    @Override
    public boolean isMenuIdValid(Long id) {
        return Objects.nonNull(menuMapper.selectById(id));
    }

    @Override
    public boolean isGroupIdValid(Long id) {
        return Objects.nonNull(menuGroupMapper.selectById(id));
    }
}
