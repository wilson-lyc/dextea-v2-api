package cn.dextea.staff.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Staff {
    @TableId(type = IdType.AUTO)
    Long id;// 员工id
    String name;// 姓名
    String namePinyin;// 姓名拼音
    String account;// 账号
    @TableField(select = false)
    String password;// 密码
    String role;// 角色
    Long storeId;// 门店id
    String phone;// 电话
    String createTime;//创建时间
    String updateTime;// 更新时间
    Boolean state;// 状态
}
