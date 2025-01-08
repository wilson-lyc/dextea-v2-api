package cn.dextea.staff.service.impl;

import cn.dextea.common.dto.ApiResponse;
import cn.dextea.common.exception.MySQLException;
import cn.dextea.staff.mapper.StaffMapper;
import cn.dextea.staff.pojo.Staff;
import cn.dextea.staff.service.StaffService;
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

    @Override
    public ApiResponse getStaffList(int current, int size) {
        Page<Staff> page=new Page<>(current,size);
        page.setRecords(staffMapper.selectPage(page, null).getRecords());
        return ApiResponse.success(page);
    }
}
