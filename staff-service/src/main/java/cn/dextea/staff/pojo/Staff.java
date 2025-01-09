package cn.dextea.staff.pojo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Staff {
    int id;
    String name;// 姓名
    String namePinyin;// 姓名拼音
    String account;// 账号
    String password;// 密码
    String role;// 角色
    String phone;// 电话
    String createTime;//创建时间
    String updateTime;// 更新时间
    int state=1;// 状态
}
