package cn.dextea.staff.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.pojo.Staff;
import cn.dextea.staff.dto.*;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.service.StaffService;
import cn.dextea.staff.util.AccountUtil;
import cn.dextea.staff.util.PasswordUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Service
public class StaffServiceImpl implements StaffService {
    @Resource
    private StaffMapper staffMapper;
    @Resource
    private AccountUtil accountUtil;
    @Resource
    private PasswordUtil passwordUtil;
    @Resource
    private StoreFeign storeFeign;

    @Override
    public ApiResponse createStaff(StaffCreateDTO data) {
        // 校验身份和隶属门店
        if (data.getIdentity() == StaffIdentity.STORE && Objects.isNull(data.getStoreId()))
            return ApiResponse.badRequest("门店员工需绑定门店");
        if(data.getIdentity() == StaffIdentity.STORE && !storeFeign.isStoreIdValid(data.getStoreId()))
            return ApiResponse.badRequest("员工绑定的门店不存在");
        // 生成账号
        Staff staff = accountUtil.create(data.getName());
        // 生成密码
        String password = passwordUtil.create();
        staff.setPassword(passwordUtil.encrypt(password));
        // 写入手机号
        staff.setPhone(data.getPhone());
        // 写入身份和隶属门店
        staff.setIdentity(data.getIdentity());
        staff.setStoreId(data.getStoreId());
        // 默认禁用
        staff.setStatus(StaffStatus.FORBIDDEN);
        // 更新db
        staffMapper.updateById(staff);
        staff.setPassword(password);
        return ApiResponse.success("员工创建成功",JSONObject.of("staff",staff));
    }

    @Override
    public ApiResponse getStaffList(int current, int size, StaffQueryDTO filter) {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAsClass(Staff.class,StaffListDTO.class)
                .eqIfExists(Staff::getId,filter.getId())
                .likeIfExists(Staff::getName,filter.getName())
                .eqIfExists(Staff::getAccount,filter.getAccount())
                .eqIfExists(Staff::getPhone,filter.getPhone())
                .eqIfExists(Staff::getStatus,filter.getStatus())
                .eqIfExists(Staff::getIdentity,filter.getIdentity())
                .eqIfExists(Staff::getStoreId,filter.getStoreId());
        IPage<Staff> page=staffMapper.selectJoinPage(
                new Page<>(current,size),
                Staff.class,
                wrapper);
        if(page.getCurrent()>page.getPages()){
            page=staffMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    Staff.class,
                    wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ApiResponse getStaffInfo(Long id) {
        Staff staff=staffMapper.selectById(id);
        if(Objects.isNull(staff)){
            return ApiResponse.notFound("不存在该员工");
        }
        return ApiResponse.success(JSONObject.of("staff",staff));
    }

    @Override
    public ApiResponse updateStaffInfo(Long id, StaffUpdateDTO data) {
        Staff staff=data.toStaff();
        if (staffMapper.updateById(staff)==0){
            return ApiResponse.notFound("不存在该员工");
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse sysResetPwd(Long id) {
        String password=passwordUtil.create();
        Staff staff=Staff.builder()
                .id(id)
                .password(passwordUtil.encrypt(password))
                .build();
        if(staffMapper.updateById(staff)==0){
            return ApiResponse.notFound("不存在该员工");
        }
        return ApiResponse.success("密码已重置",JSONObject.of("password",password));
    }

    @Override
    public ApiResponse updateStaffPwd(Long id, StaffUpdatePwdDTO data) {
        // 校验旧密码
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .select(Staff::getPassword)
                .eq(Staff::getId,id);
        String dbPwd=staffMapper.selectJoinOne(String.class,wrapper);
        if (!Objects.equals(passwordUtil.encrypt(data.getOldPwd()), dbPwd)){
            return ApiResponse.badRequest("原密码错误");
        }
        // 新密码写入db
        Staff staff=Staff.builder()
                .id(id)
                .password(passwordUtil.encrypt(data.getNewPwd()))
                .build();
        if (staffMapper.updateById(staff)==0){
            return ApiResponse.notFound("不存在该员工");
        }
        return ApiResponse.success("密码修改成功");
    }

    @Override
    public ApiResponse login(StaffLoginDTO data) {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAsClass(Staff.class,StaffDTO.class)
                .eq(Staff::getAccount,data.getAccount())
                .eq(Staff::getPassword,passwordUtil.encrypt(data.getPassword()));
        Staff staff=staffMapper.selectJoinOne(wrapper);
        // 账号或密码错误
        if(Objects.isNull(staff)){
            return ApiResponse.badRequest("账号或密码错误");
        }
        // 账号被禁用
        if(staff.getStatus()==StaffStatus.FORBIDDEN){
            return ApiResponse.badRequest("账号已禁用");
        }
        // 创建token
        StpUtil.login(staff.getId());
        SaTokenInfo token=StpUtil.getTokenInfo();
        return ApiResponse.success("登录成功",JSONObject.of(
                "staff",staff,
                "token",token.getTokenValue()));
    }
}
