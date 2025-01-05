package cn.dextea.staff.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.dto.ResponseDTO;
import cn.dextea.common.exception.MySQLException;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.service.RegisterService;
import cn.dextea.staff.util.AccountUtil;
import cn.dextea.staff.util.PasswordUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private AccountUtil accountUtil;
    @Autowired
    private PasswordUtil passwordUtil;

    @Override
    public ApiResponse register(String name, String role, String phone) {
        // 创建账号
        String account = accountUtil.create(name);
        // 生成密码
        String password = passwordUtil.create();
        Staff staff = new Staff();
        staff.setName(name);
        staff.setRole(role);
        staff.setAccount(account);
        staff.setPassword(passwordUtil.encrypt(password));
        // 更新员工信息
        try{
            staffMapper.update(staff,new QueryWrapper<Staff>().eq("account",account));
        }catch (Exception e){
            String msg=String.format("Failed to update staff, account=%s",account);
            throw new MySQLException(msg,e);
        }
        JSONObject data=new JSONObject();
        data.put("account",account);
        data.put("password",password);
        return ApiResponse.success("注册成功",data);
    }
}
