package cn.dextea.staff.service.impl;

import cn.dextea.common.model.staff.StaffModel;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.service.InternalService;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public List<StaffModel> getStaffInIds(List<Long> ids) {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAsClass(Staff.class,StaffModel.class)
                .in(Staff::getId,ids);
        return staffMapper.selectJoinList(StaffModel.class,wrapper);
    }
}
