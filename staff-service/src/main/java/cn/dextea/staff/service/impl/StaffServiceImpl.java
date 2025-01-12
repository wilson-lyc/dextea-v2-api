package cn.dextea.staff.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.staff.dto.UpdateStaffDTO;
import cn.dextea.staff.dto.RegisterDTO;
import cn.dextea.staff.dto.RegisterResDTO;
import cn.dextea.staff.dto.SearchStaffDTO;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.service.StaffService;
import cn.dextea.staff.util.AccountUtil;
import cn.dextea.staff.util.PasswordUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
    public ApiResponse create(RegisterDTO data) {
        // 创建账号，新账号已写入数据库
        String account = accountUtil.create(data.getName());
        // 生成密码
        String password = passwordUtil.create();
        Staff staff = new Staff();
        staff.setName(data.getName());
        staff.setRole(data.getRole());
        staff.setAccount(account);
        staff.setPhone(data.getPhone());
        staff.setPassword(passwordUtil.encrypt(password));// 加密密码
        staff.setState(true);
        // 更新员工信息
        staffMapper.update(staff,new QueryWrapper<Staff>().eq("account",account));
        return ApiResponse.success("注册成功",new RegisterResDTO(account,password));
    }

    @Override
    public ApiResponse getStaffById(Long id) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        Staff staff=staffMapper.selectOne(wrapper);
        if(staff==null){
            String msg = String.format("不存该员工，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success(staff);
    }

    @Override
    public ApiResponse search(int current,int size, SearchStaffDTO condition) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        if(condition.getId()!=null){
            wrapper.eq("id",condition.getId());
        }
        if(condition.getName()!=null){
            wrapper.eq("name",condition.getName());
        }
        if(condition.getAccount()!=null){
            wrapper.eq("account",condition.getAccount());
        }
        if(condition.getRole()!=null){
            wrapper.eq("role",condition.getRole());
        }
        if(condition.getStoreId()!=null){
            wrapper.eq("store_id",condition.getStoreId());
        }
        if(condition.getPhone()!=null){
            wrapper.eq("phone",condition.getPhone());
        }
        if(condition.getState()!=null){
            wrapper.eq("state",condition.getState());
        }
        Page<Staff> page=new Page<>(current,size);
        page=staffMapper.selectPage(page,wrapper);
        return ApiResponse.success(page);
    }

    @Override
    public ApiResponse resetPwd(Long id) {
        String password=passwordUtil.create();
        Staff staff=new Staff();
        staff.setPassword(passwordUtil.encrypt(passwordUtil.create()));
        int num=staffMapper.update(staff,new QueryWrapper<Staff>().eq("id",id));
        if(num==0){
            String msg=String.format("未找到该员工，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        JSONObject data=new JSONObject();
        data.put("password",password);
        return ApiResponse.success("密码已重置",data);
    }

    @Override
    public ApiResponse update(Long id, UpdateStaffDTO data) {
        System.out.println(data);
        Staff staff=new Staff();
        staff.setId(id);
        staff.setRole(data.getRole());
        staff.setStoreId(data.getStoreId());
        staff.setPhone(data.getPhone());
        staff.setState(data.getState());
        int num=staffMapper.update(staff,new QueryWrapper<Staff>().eq("id",id));
        if (num==0){
            String msg=String.format("未找到该员工，id=%d",id);
            return ApiResponse.notFound(msg);
        }
        return ApiResponse.success("信息已更新",null);
    }
}
