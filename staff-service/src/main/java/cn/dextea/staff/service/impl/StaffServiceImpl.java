package cn.dextea.staff.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.feign.StaffFeign;
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
import org.apache.ibatis.javassist.NotFoundException;
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
    @Resource
    private StaffFeign staffFeign;

    @Override
    public ApiResponse createStaff(StaffCreateDTO data){
        // 校验身份和隶属门店
        if (data.getIdentity() == StaffIdentity.STORE.getValue() && Objects.isNull(data.getStoreId()))
            throw new IllegalArgumentException("缺少门店ID");
        if(data.getIdentity() == StaffIdentity.STORE.getValue() && !storeFeign.isStoreIdValid(data.getStoreId()))
            throw new IllegalArgumentException("storeId错误");
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
        staff.setStatus(StaffStatus.FORBIDDEN.getValue());
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
    public ApiResponse getStaffInfo(Long id) throws NotFoundException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAsClass(Staff.class,StaffDTO.class)
                .eq(Staff::getId,id);
        StaffDTO staff=staffMapper.selectJoinOne(StaffDTO.class,wrapper);
        if(Objects.isNull(staff)){
            throw new NotFoundException("员工不存在");
        }
        // 获取门店名
        if(staff.getIdentity()==StaffIdentity.STORE.getValue()){
            String storeName=storeFeign.getStoreName(staff.getStoreId());
            staff.setStoreName(storeName);
        }
        return ApiResponse.success(JSONObject.of("staff",staff));
    }

    @Override
    public ApiResponse getStaffStatus(Long id) throws NotFoundException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .select(Staff::getStatus)
                .eq(Staff::getId,id);
        Integer status=staffMapper.selectJoinOne(Integer.class,wrapper);
        if (Objects.isNull(status))
            throw new NotFoundException("员工不存在");
        return ApiResponse.success(JSONObject.of("status",status));
    }

    @Override
    public ApiResponse updateStaffInfo(Long id, StaffUpdateDTO data) throws NotFoundException {
        Staff staff=data.toStaff();
        staff.setId(id);
        if (staffMapper.updateById(staff)==0){
            throw new NotFoundException("员工不存在");
        }
        return ApiResponse.success("更新成功");
    }

    @Override
    public ApiResponse updateStaffStatus(Long id, Integer status) throws NotFoundException {
        Staff staff=Staff.builder()
                .id(id)
                .status(status)
                .build();
        if(staffMapper.updateById(staff)==0){
            throw new NotFoundException("员工不存在");
        }
        return ApiResponse.success("状态更新成功");
    }

    @Override
    public ApiResponse sysResetPwd(Long id) throws NotFoundException {
        String password=passwordUtil.create();
        Staff staff=Staff.builder()
                .id(id)
                .password(passwordUtil.encrypt(password))
                .build();
        if(staffMapper.updateById(staff)==0){
            throw new NotFoundException("员工不存在");
        }
        return ApiResponse.success("密码已重置",JSONObject.of("password",password));
    }

    @Override
    public ApiResponse updateStaffPwd(Long id, StaffUpdatePwdDTO data) throws NotFoundException {
        // 校验id
        if(!staffFeign.isStaffIdValid(id))
            throw new NotFoundException("员工不存在");
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
        staffMapper.updateById(staff);
        return ApiResponse.success("密码修改成功");
    }

    @Override
    public ApiResponse login(StaffLoginDTO data) throws IllegalAccessException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAsClass(Staff.class,StaffDTO.class)
                .eq(Staff::getAccount,data.getAccount())
                .eq(Staff::getPassword,passwordUtil.encrypt(data.getPassword()));
        Staff staff=staffMapper.selectJoinOne(wrapper);
        // 账号或密码错误
        if(Objects.isNull(staff)){
            throw new IllegalArgumentException("账号或密码错误");
        }
        // 账号被禁用
        if(staff.getStatus()==StaffStatus.FORBIDDEN.getValue()){
            throw new IllegalAccessException("账号被禁用");
        }
        // 创建token
        StpUtil.login(staff.getId());
        SaTokenInfo token=StpUtil.getTokenInfo();
        return ApiResponse.success("登录成功",JSONObject.of(
                "staff",staff,
                "token",token.getTokenValue()));
    }
}
