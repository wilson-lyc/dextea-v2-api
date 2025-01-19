package cn.dextea.staff.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.StaffLoginResDTO;
import cn.dextea.staff.dto.*;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.service.StaffService;
import cn.dextea.staff.util.AccountUtil;
import cn.dextea.staff.util.PasswordUtil;
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

    @Override
    public ApiResponse create(CreateStaffDTO data) {
        // 创建账号，新账号已写入数据库
        String account = accountUtil.create(data.getName());
        // 生成密码
        String password = passwordUtil.create();
        Staff staff =Staff.builder()
                .name(data.getName())
                .account(account)
                .phone(data.getPhone())
                .password(passwordUtil.encrypt(password))
                .build();
        // 更新员工信息
        staffMapper.update(staff,new QueryWrapper<Staff>().eq("account",account));
        return ApiResponse.success("员工已创建",JSONObject.of("account",account,"password",password));
    }

    @Override
    public ApiResponse getStaffById(Long id) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        Staff staff=staffMapper.selectOne(wrapper);
        if(staff==null){
            String msg = String.format("员工不存在，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success(JSONObject.of("staff",staff));
    }

    @Override
    public ApiResponse getStaffList(int current, int size, SearchStaffDTO condition) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        if(condition.getId()!=null){
            wrapper.eq("id",condition.getId());
        }
        if(condition.getName()!=null&&!condition.getName().isBlank()){
            wrapper.eq("name",condition.getName());
        }
        if(condition.getAccount()!=null&&!condition.getAccount().isBlank()){
            wrapper.eq("account",condition.getAccount());
        }
        if(condition.getPhone()!=null&&!condition.getPhone().isBlank()){
            wrapper.eq("phone",condition.getPhone());
        }
        if(condition.getStatus()!=null){
            wrapper.eq("state",condition.getStatus());
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
    public ApiResponse update(Long id, UpdateStaffDTO data) {
        System.out.println(data);
        Staff staff=Staff.builder()
                .id(id)
                .phone(data.getPhone())
                .status(data.getStatus())
                .build();
        int num=staffMapper.update(staff,new QueryWrapper<Staff>().eq("id",id));
        if (num==0){
            String msg=String.format("未找到该员工，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success();
    }

    @Override
    public ApiResponse login(CheckPwdDTO data) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        wrapper.eq("account",data.getAccount());
        wrapper.eq("password",passwordUtil.encrypt(data.getPassword()));
        Staff staff=staffMapper.selectOne(wrapper);
        // 账号或密码错误
        if(staff==null){
            return ApiResponse.badRequest("账号或密码错误");
        }
        // 账号被禁用
        if(!staff.getStatus()){
            return ApiResponse.badRequest("账号已被禁用");
        }
        StaffLoginResDTO res=StaffLoginResDTO.builder()
                .id(staff.getId())
                .name(staff.getName())
                .account(staff.getAccount())
                .build();
        return ApiResponse.success("密码正确",JSONObject.of("staff",res));
    }
}
