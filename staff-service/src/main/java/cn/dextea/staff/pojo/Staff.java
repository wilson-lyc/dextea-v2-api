package cn.dextea.staff.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("s_staff")
public class Staff {
    @TableId(type = IdType.AUTO)
    Long id;//员工ID
    String name;//姓名
    String namePinyin;//姓名拼音
    String account;//账号
    @TableField(select = false)
    String password;//密码
    String phone;//手机号
    Integer status;//状态
    Integer identity;//身份
    Long storeId;//隶属门店ID
    String createTime;//创建时间
    String updateTime;//更新时间
}
