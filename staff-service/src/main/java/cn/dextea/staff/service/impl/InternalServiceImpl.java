package cn.dextea.staff.service.impl;

import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.service.InternalService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class InternalServiceImpl implements InternalService {
    @Resource
    private StaffMapper staffMapper;

    @Override
    public boolean isStaffIdValid(Long id) {
        return Objects.nonNull(staffMapper.selectById(id));
    }
}
