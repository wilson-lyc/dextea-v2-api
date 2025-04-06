package cn.dextea.staff.service.impl;

import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.feign.StaffFeign;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.model.staff.StaffModel;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.model.*;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.service.StaffService;
import cn.dextea.staff.util.AccountUtil;
import cn.dextea.staff.util.PasswordUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
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
            throw new IllegalArgumentException("门店需要绑定门店");
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
    public DexteaApiResponse<IPage<StaffModel>> getStaffList(int current, int size, StaffFilter filter) {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAsClass(Staff.class, StaffModel.class)
                //搜索条件
                .eqIfExists(Staff::getId,filter.getId())
                .likeIfExists(Staff::getName,filter.getName())
                .eqIfExists(Staff::getAccount,filter.getAccount())
                .eqIfExists(Staff::getPhone,filter.getPhone())
                .eqIfExists(Staff::getStatus,filter.getStatus())
                .eqIfExists(Staff::getIdentity,filter.getIdentity())
                .eqIfExists(Staff::getStoreId,filter.getStoreId());
        IPage<StaffModel> page=staffMapper.selectJoinPage(
                new Page<>(current,size),
                StaffModel.class,
                wrapper);
        if(page.getCurrent()>page.getPages()){
            page=staffMapper.selectJoinPage(
                    new Page<>(page.getPages(),size),
                    StaffModel.class,
                    wrapper);
        }
        return DexteaApiResponse.success(page);
    }

    @Override
    public DexteaApiResponse<StaffModel> getStaffDetail(Long id) throws NotFoundException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .eq(Staff::getId,id)
                .selectAsClass(Staff.class,StaffModel.class);
        StaffModel staff=staffMapper.selectJoinOne(StaffModel.class,wrapper);
        if(Objects.isNull(staff))
            throw new NotFoundException("员工不存在");
        // 门店员工
        if (staff.getIdentity().equals(StaffIdentity.STORE.getValue())){
            staff.setStoreName(storeFeign.getStoreName(staff.getStoreId()));
        }
        return DexteaApiResponse.success(staff);
    }

    @Override
    public DexteaApiResponse<StaffModel> getStaffStatus(Long id) throws NotFoundException {
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAs(Staff::getStatus,StaffModel::getStatus)
                .eq(Staff::getId,id);
        StaffModel staff=staffMapper.selectJoinOne(StaffModel.class,wrapper);
        if (Objects.isNull(staff))
            throw new NotFoundException("员工不存在");
        return DexteaApiResponse.success(staff);
    }

    @Override
    public DexteaApiResponse<Void> updateStaffDetail(Long id, StaffUpdateDetailRequest data) throws NotFoundException {
        Staff staff=data.toStaff(id);
        if (staffMapper.updateById(staff)==0)
            throw new NotFoundException("员工不存在");
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> updateStaffStatus(Long id, Integer status) throws NotFoundException {
        LambdaUpdateWrapper<Staff> wrapper=new LambdaUpdateWrapper<Staff>()
                .set(Staff::getStatus,status)
                .eq(Staff::getId,id);
        if(staffMapper.update(wrapper)==0)
            throw new NotFoundException("员工不存在");
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<StaffResetPasswordResponse> sysResetPwd(Long id) throws NotFoundException {
        String password=passwordUtil.create();
        LambdaUpdateWrapper<Staff> wrapper=new LambdaUpdateWrapper<Staff>()
                .set(Staff::getPassword,passwordUtil.encrypt(password))
                .eq(Staff::getId,id);
        if(staffMapper.update(wrapper)==0){
            throw new NotFoundException("员工不存在");
        }
        return DexteaApiResponse.success(new StaffResetPasswordResponse(password));
    }

    @Override
    public DexteaApiResponse<Void> updateStaffPwd(Long id, StaffUpdatePwdRequest data) throws NotFoundException {
        // 校验旧密码
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .select(Staff::getPassword)
                .eq(Staff::getId,id);
        String dbPwd=staffMapper.selectJoinOne(String.class,wrapper);
        if(Objects.isNull(dbPwd))
            throw new NotFoundException("员工不存在");
        if (!Objects.equals(passwordUtil.encrypt(data.getOldPwd()), dbPwd)){
            throw new IllegalArgumentException("原密码错误");
        }
        // 新密码写入db
        LambdaUpdateWrapper<Staff> uWrapper=new LambdaUpdateWrapper<Staff>()
                .set(Staff::getPassword,passwordUtil.encrypt(data.getNewPwd()))
                .eq(Staff::getId,id);
        staffMapper.update(uWrapper);
        return DexteaApiResponse.success();
    }
}
