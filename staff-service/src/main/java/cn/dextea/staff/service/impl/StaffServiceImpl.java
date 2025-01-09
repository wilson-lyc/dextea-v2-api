package cn.dextea.staff.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.exception.MySQLException;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.service.StaffService;
import cn.dextea.staff.util.PasswordUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author Lai Yongchao
 */
@Service
public class StaffServiceImpl implements StaffService {
    @Autowired
    private StaffMapper staffMapper;
    @Autowired
    private PasswordUtil passwordUtil;
    /**
     * 根据id获取员工信息
     * @param id 员工id
     * @return 员工信息
     */
    @Override
    public ApiResponse getStaffById(int id) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        wrapper.eq("id",id);
        try{
            Staff staff=staffMapper.selectOne(wrapper);
            if(staff==null){
                String msg = String.format("Staff not found with id: %d", id);
                return ApiResponse.notFound(msg);
            }
            return ApiResponse.success(staff);
        }catch (Exception e){
            String msg=String.format("Failed to get staff by id, id=%d",id);
            throw new MySQLException(msg,e);
        }
    }

    /**
     * 获取员工列表
     * @param current 页码
     * @param size 分页大小
     * @param id 员工ID
     * @param role 角色
     * @param phone 手机号
     * @param state 状态
     * @return 员工列表
     */
    @Override
    public ApiResponse getStaffList(int current, int size, int id, String role, String phone, int state) {
        QueryWrapper<Staff> wrapper=new QueryWrapper<>();
        if(id!=-1){
            wrapper.eq("id",id);
        }
        if(!role.isEmpty()){
            wrapper.eq("role",role);
        }
        if(!phone.isEmpty()){
            wrapper.eq("phone",phone);
        }
        if(state!=-1){
            wrapper.eq("state",state);
        }
        Page<Staff> page=new Page<>(current,size);
        try{
            page.setRecords(staffMapper.selectPage(page, wrapper).getRecords());
        }catch (Exception e){
            String msg=String.format("Failed to get staff list, current=%d, size=%d, id=%d, role=%s, phone=%s, state=%d",current,size,id,role,phone,state);
            throw new MySQLException(msg,e);
        }
        return ApiResponse.success(page);
    }

    @Override
    public ApiResponse resetPwd(int id) {
        String password=passwordUtil.create();
        try{
            Staff staff=new Staff();
            staff.setPassword(passwordUtil.encrypt(passwordUtil.create()));
            int num=staffMapper.update(staff,new QueryWrapper<Staff>().eq("id",id));
            if(num==0){
                String msg=String.format("Staff not found with id: %d", id);
                return ApiResponse.notFound(msg);
            }
            JSONObject data=new JSONObject();
            data.put("password",password);
            return ApiResponse.success("密码重置成功",data);
        }catch (Exception e){
            String msg=String.format("Failed to reset password, id=%d",id);
            throw new MySQLException(msg,e);
        }
    }
}
