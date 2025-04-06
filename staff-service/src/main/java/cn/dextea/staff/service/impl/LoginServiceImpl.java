package cn.dextea.staff.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.model.staff.StaffModel;
import cn.dextea.staff.model.StaffLoginRequest;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.service.LoginService;
import cn.dextea.staff.util.PasswordUtil;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Service
public class LoginServiceImpl implements LoginService {
    @Resource
    private PasswordUtil passwordUtil;
    @Resource
    private StaffMapper staffMapper;
    @Override
    public DexteaApiResponse<StaffModel> staffLogin(StaffLoginRequest data) throws IllegalAccessException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAs(Staff::getId, StaffModel::getId)
                .selectAs(Staff::getName, StaffModel::getName)
                .selectAs(Staff::getStatus,StaffModel::getStatus)
                .selectAs(Staff::getIdentity,StaffModel::getIdentity)
                .eq(Staff::getAccount,data.getAccount())
                .eq(Staff::getPassword,passwordUtil.encrypt(data.getPassword()));
        StaffModel staff=staffMapper.selectJoinOne(StaffModel.class,wrapper);
        // 账号或密码错误
        if(Objects.isNull(staff)){
            throw new IllegalArgumentException("账号或密码错误");
        }
        // 账号被禁用
        if(staff.getStatus().equals(StaffStatus.FORBIDDEN.getValue())){
            throw new IllegalAccessException("账号被禁用");
        }
        // 创建token
        StpUtil.login(staff.getId());
        SaTokenInfo token=StpUtil.getTokenInfo();
        staff.setToken(token.getTokenValue());
        return DexteaApiResponse.success(staff);
    }
}
