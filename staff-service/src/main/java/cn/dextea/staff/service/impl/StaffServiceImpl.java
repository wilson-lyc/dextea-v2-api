package cn.dextea.staff.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.DexteaApiResponse;
import cn.dextea.common.feign.StaffFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.pojo.Staff;
import cn.dextea.common.pojo.Store;
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
    public DexteaApiResponse<StaffCreateResponse> createStaff(StaffCreateRequest data){
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
        return DexteaApiResponse.success(
                StaffCreateResponse.builder()
                        .id(staff.getId())
                        .name(staff.getName())
                        .account(staff.getAccount())
                        .password(password)
                        .build());
    }

    @Override
    public DexteaApiResponse<IPage<StaffListResponse>> getStaffList(int current, int size, StaffFilter filter) {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAsClass(Staff.class, StaffListResponse.class)
                //搜索条件
                .eqIfExists(Staff::getId,filter.getId())
                .likeIfExists(Staff::getName,filter.getName())
                .eqIfExists(Staff::getAccount,filter.getAccount())
                .eqIfExists(Staff::getPhone,filter.getPhone())
                .eqIfExists(Staff::getStatus,filter.getStatus())
                .eqIfExists(Staff::getIdentity,filter.getIdentity())
                .eqIfExists(Staff::getStoreId,filter.getStoreId());
        IPage<StaffListResponse> page=staffMapper.selectJoinPage(
                new Page<>(current,size),
                StaffListResponse.class,
                wrapper);
        if(page.getCurrent()>page.getPages()){
            page=staffMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    StaffListResponse.class,
                    wrapper);
        }
        return DexteaApiResponse.success(page);
    }

    @Override
    public DexteaApiResponse<StaffInfoResponse> getStaffInfo(Long id) throws NotFoundException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .eq(Staff::getId,id)
                .selectAsClass(Staff.class,StaffInfoResponse.class)
                .leftJoin(Store.class,Store::getId,Staff::getStoreId)
                .selectAs(Store::getName,StaffInfoResponse::getStoreName);
        StaffInfoResponse staff=staffMapper.selectJoinOne(StaffInfoResponse.class,wrapper);
        if(Objects.isNull(staff))
            throw new NotFoundException("员工不存在");
        return DexteaApiResponse.success(staff);
    }

    @Override
    public DexteaApiResponse<StaffStatusResponse> getStaffStatus(Long id) throws NotFoundException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .select(Staff::getStatus)
                .eq(Staff::getId,id);
        Integer status=staffMapper.selectJoinOne(Integer.class,wrapper);
        if (Objects.isNull(status))
            throw new NotFoundException("员工不存在");
        StaffStatusResponse staffStatus=new StaffStatusResponse(status);
        return DexteaApiResponse.success(staffStatus);
    }

    @Override
    public DexteaApiResponse<StaffInfoResponse> updateStaffInfo(Long id, StaffUpdateRequest data) throws NotFoundException {
        Staff staff=data.toStaff(id);
        if (staffMapper.updateById(staff)==0)
            throw new NotFoundException("员工不存在");
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<StaffInfoResponse> updateStaffStatus(Long id, Integer status) throws NotFoundException {
        Staff staff=Staff.builder()
                .id(id)
                .status(status)
                .build();
        if(staffMapper.updateById(staff)==0)
            throw new NotFoundException("员工不存在");
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<StaffResetPasswordResponse> sysResetPwd(Long id) throws NotFoundException {
        String password=passwordUtil.create();
        Staff staff=Staff.builder()
                .id(id)
                .password(passwordUtil.encrypt(password))
                .build();
        if(staffMapper.updateById(staff)==0){
            throw new NotFoundException("员工不存在");
        }
        return DexteaApiResponse.success(new StaffResetPasswordResponse(password));
    }

    @Override
    public DexteaApiResponse<StaffInfoResponse> updateStaffPwd(Long id, StaffUpdatePwdRequest data) throws NotFoundException {
        // 校验id
        if(!staffFeign.isStaffIdValid(id))
            throw new NotFoundException("员工不存在");
        // 校验旧密码
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .select(Staff::getPassword)
                .eq(Staff::getId,id);
        String dbPwd=staffMapper.selectJoinOne(String.class,wrapper);
        if (!Objects.equals(passwordUtil.encrypt(data.getOldPwd()), dbPwd)){
            throw new IllegalArgumentException("原密码错误");
        }
        // 新密码写入db
        Staff staff=Staff.builder()
                .id(id)
                .password(passwordUtil.encrypt(data.getNewPwd()))
                .build();
        staffMapper.updateById(staff);
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<StaffLoginResponse> staffLogin(StaffLoginRequest data) throws IllegalAccessException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAsClass(Staff.class,StaffInfoResponse.class)
                .eq(Staff::getAccount,data.getAccount())
                .eq(Staff::getPassword,passwordUtil.encrypt(data.getPassword()));
        StaffInfoResponse staff=staffMapper.selectJoinOne(StaffInfoResponse.class,wrapper);
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
        return DexteaApiResponse.success(StaffLoginResponse.builder()
                .staff(staff)
                .token(token)
                .build());
    }
}
