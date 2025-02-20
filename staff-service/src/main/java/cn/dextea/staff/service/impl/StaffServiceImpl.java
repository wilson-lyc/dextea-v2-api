package cn.dextea.staff.service.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.*;
import cn.dextea.staff.feign.RoleFeign;
import cn.dextea.staff.feign.StoreFeign;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.pojo.StaffStatus;
import cn.dextea.staff.service.StaffService;
import cn.dextea.staff.util.AccountUtil;
import cn.dextea.staff.util.PasswordUtil;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Lai Yongchao
 */
@Slf4j
@Service
public class StaffServiceImpl implements StaffService {
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private PasswordUtil passwordUtil;
    @Autowired
    private StoreFeign storeFeign;
    @Autowired
    private RoleFeign roleFeign;

    @Override
    public ApiResponse create(StaffCreateDTO data) {
        // 创建账号，新账号已写入数据库
        String account = accountUtil.create(data.getName());
        // 生成密码
        String password = passwordUtil.create();
        Staff staff =data.toStaff();
        staff.setAccount(account);
        staff.setPassword(passwordUtil.encrypt(password));
        // 更新员工信息
        staffMapper.update(staff,new QueryWrapper<Staff>().eq("account",account));
        return ApiResponse.success("员工已创建",JSONObject.of("account",account,"password",password));
    }

    @Override
    public ApiResponse getById(Long id) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        Staff staff=staffMapper.selectOne(wrapper);
        if(staff==null){
            String msg = String.format("员工不存在，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        if(staff.getSide()==2 && staff.getStoreId()!=null){
            ApiResponse res=storeFeign.getStoreById(staff.getStoreId());
            staff.setStoreName(res.getData().getJSONObject("store").getString("name"));
        }
        return ApiResponse.success(JSONObject.of("staff",staff));
    }

    @Override
    public ApiResponse getList(int current, int size, StaffQueryDTO filter) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        if(filter.getId()!=null){
            wrapper.eq("id",filter.getId());
        }
        if(filter.getName()!=null&&!filter.getName().isBlank()){
            wrapper.like("name",filter.getName());
        }
        if(filter.getAccount()!=null&&!filter.getAccount().isBlank()){
            wrapper.like("account",filter.getAccount());
        }
        if(filter.getPhone()!=null&&!filter.getPhone().isBlank()){
            wrapper.eq("phone",filter.getPhone());
        }
        if(filter.getStatus()!=null){
            wrapper.eq("status",filter.getStatus());
        }
        if(filter.getSide()!=null){
            wrapper.eq("side",filter.getSide());
        }
        if(filter.getStoreId()!=null){
            wrapper.eq("store_id",filter.getStoreId());
        }
        Page<Staff> page=new Page<>(current,size);
        page=staffMapper.selectPage(page,wrapper);
        // 如果当前页码大于总页数，返回最后一页
        if(page.getCurrent()>page.getPages()){
            page.setCurrent(page.getPages());
            page=staffMapper.selectPage(page,wrapper);
        }
        return ApiResponse.success(JSONObject.from(page));
    }

    @Override
    public ApiResponse resetPwd(Long id) {
        String password=passwordUtil.create();
        Staff staff=Staff.builder()
                .password(passwordUtil.encrypt(password))
                .build();
        int num=staffMapper.update(staff,new QueryWrapper<Staff>().eq("id",id));
        if(num==0){
            String msg=String.format("员工不存在，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success("密码已重置",JSONObject.of("password",password));
    }

    @Override
    public ApiResponse updateBase(Long id, StaffUpdateDTO data) {
        System.out.println(data);
        Staff staff=data.toStaff();
        int num=staffMapper.update(staff,new QueryWrapper<Staff>().eq("id",id));
        if (num==0){
            String msg=String.format("未找到该员工，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse login(StaffLoginDTO data) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        wrapper.eq("account",data.getAccount());
        wrapper.eq("password",passwordUtil.encrypt(data.getPassword()));
        Staff staff=staffMapper.selectOne(wrapper);
        // 账号或密码错误
        if(staff==null){
            return ApiResponse.badRequest("账号或密码错误");
        }
        // 账号被禁用
        if(staff.getStatus()==0){
            return ApiResponse.badRequest("账号已被禁用");
        }
        // 创建token
        StpUtil.login(staff.getId());
        SaTokenInfo token=StpUtil.getTokenInfo();
        // 获取role
        ApiResponse res=roleFeign.getStaffRoleKeyByUid(staff.getId());
        JSONArray role = null;
        if (res.getCode()==200){
            role=res.getData().getJSONArray("keys");
        }
        return ApiResponse.success("登录成功",JSONObject.of(
                "user",staff,
                "token",token.getTokenValue(),
                "role",role));
    }

    @Override
    public ApiResponse active(Long id) {
        Staff staff=Staff.builder()
                .status(StaffStatus.Enable.getCode())
                .build();
        int num=staffMapper.update(staff,new QueryWrapper<Staff>().eq("id",id));
        if(num==0){
            String msg=String.format("员工不存在，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success("账号已激活");
    }

    @Override
    public ApiResponse ban(Long id) {
        Staff staff=Staff.builder()
                .status(StaffStatus.Disable.getCode())
                .build();
        int num=staffMapper.update(staff,new QueryWrapper<Staff>().eq("id",id));
        if(num==0){
            String msg=String.format("员工不存在，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success("账号已禁用");
    }

    @Override
    public ApiResponse updatePwd(Long id, PwdUpdateDTO data) {
        Staff staff=Staff.builder()
                .id(id)
                .password(passwordUtil.encrypt(data.getNewPwd()))
                .build();
        int num=staffMapper.updateById(staff);
        if (num==0){
            String msg=String.format("未找到该员工，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success();
    }
}
