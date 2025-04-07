package cn.dextea.staff.service.impl;

import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import cn.dextea.common.model.common.DexteaApiResponse;
import cn.dextea.common.feign.StoreFeign;
import cn.dextea.common.model.staff.StaffModel;
import cn.dextea.staff.code.StaffErrorCode;
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
    public DexteaApiResponse<StaffCreateResponse> createStaff(StaffCreateRequest data){
        // 校验身份和隶属门店
        if (data.getIdentity() == StaffIdentity.STORE.getValue() && Objects.isNull(data.getStoreId())){
            return DexteaApiResponse.fail(StaffErrorCode.STORE_ID_NULL.getCode(),StaffErrorCode.STORE_ID_NULL.getMsg());
        }
        if(data.getIdentity() == StaffIdentity.STORE.getValue() && !storeFeign.isStoreIdValid(data.getStoreId())){
            return DexteaApiResponse.fail(StaffErrorCode.STORE_ID_ILLEGAL.getCode(),StaffErrorCode.STORE_ID_ILLEGAL.getMsg());
        }
        // 生成账号
        Staff staff = null;
        try {
            staff = accountUtil.create(data.getName());
        } catch (Exception e) {
            return DexteaApiResponse.fail(StaffErrorCode.CREATE_ACCOUNT_FAIL.getCode(),StaffErrorCode.CREATE_ACCOUNT_FAIL.getMsg());
        }
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
    public DexteaApiResponse<StaffModel> getStaffDetail(Long id){
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .eq(Staff::getId,id)
                .selectAsClass(Staff.class,StaffModel.class);
        StaffModel staff=staffMapper.selectJoinOne(StaffModel.class,wrapper);
        if(Objects.isNull(staff)) {
            return DexteaApiResponse.fail(StaffErrorCode.NOT_FOUND.getCode(),StaffErrorCode.NOT_FOUND.getMsg());
        }
        // 门店员工
        if (staff.getIdentity().equals(StaffIdentity.STORE.getValue())){
            staff.setStoreName(storeFeign.getStoreName(staff.getStoreId()));
        }
        return DexteaApiResponse.success(staff);
    }

    @Override
    public DexteaApiResponse<StaffModel> getStaffStatus(Long id){
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .selectAs(Staff::getStatus,StaffModel::getStatus)
                .eq(Staff::getId,id);
        StaffModel staff=staffMapper.selectJoinOne(StaffModel.class,wrapper);
        if(Objects.isNull(staff)) {
            return DexteaApiResponse.fail(StaffErrorCode.NOT_FOUND.getCode(),StaffErrorCode.NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(staff);
    }

    @Override
    public DexteaApiResponse<Void> updateStaffDetail(Long id, StaffUpdateDetailRequest data){
        Staff staff=data.toStaff(id);
        if (staffMapper.updateById(staff)==0){
            return DexteaApiResponse.fail(StaffErrorCode.NOT_FOUND.getCode(),StaffErrorCode.NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<Void> updateStaffStatus(Long id, Integer status){
        LambdaUpdateWrapper<Staff> wrapper=new LambdaUpdateWrapper<Staff>()
                .set(Staff::getStatus,status)
                .eq(Staff::getId,id);
        if(staffMapper.update(wrapper)==0){
            return DexteaApiResponse.fail(StaffErrorCode.NOT_FOUND.getCode(),StaffErrorCode.NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success();
    }

    @Override
    public DexteaApiResponse<StaffResetPasswordResponse> sysResetPwd(Long id){
        String password=passwordUtil.create();
        LambdaUpdateWrapper<Staff> wrapper=new LambdaUpdateWrapper<Staff>()
                .set(Staff::getPassword,passwordUtil.encrypt(password))
                .eq(Staff::getId,id);
        if(staffMapper.update(wrapper)==0){
            return DexteaApiResponse.fail(StaffErrorCode.NOT_FOUND.getCode(),StaffErrorCode.NOT_FOUND.getMsg());
        }
        return DexteaApiResponse.success(new StaffResetPasswordResponse(password));
    }

    @Override
    public DexteaApiResponse<Void> updateStaffPwd(Long id, StaffUpdatePwdRequest data){
        // 校验旧密码
        MPJLambdaWrapper<Staff> wrapper=new MPJLambdaWrapper<Staff>()
                .select(Staff::getPassword)
                .eq(Staff::getId,id);
        String dbPwd=staffMapper.selectJoinOne(String.class,wrapper);
        if(Objects.isNull(dbPwd)){
            return DexteaApiResponse.fail(StaffErrorCode.NOT_FOUND.getCode(),StaffErrorCode.NOT_FOUND.getMsg());
        }
        if (!Objects.equals(passwordUtil.encrypt(data.getOldPwd()), dbPwd)){
            return DexteaApiResponse.fail(StaffErrorCode.OLD_PASSWORD_ILLEGAL.getCode(),StaffErrorCode.OLD_PASSWORD_ILLEGAL.getMsg());
        }
        // 新密码写入db
        LambdaUpdateWrapper<Staff> uWrapper=new LambdaUpdateWrapper<Staff>()
                .set(Staff::getPassword,passwordUtil.encrypt(data.getNewPwd()))
                .eq(Staff::getId,id);
        staffMapper.update(uWrapper);
        return DexteaApiResponse.success();
    }
}
