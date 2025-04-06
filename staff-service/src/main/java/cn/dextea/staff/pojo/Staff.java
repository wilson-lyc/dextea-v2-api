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
    private Long id;//员工ID
    private String name;//姓名
    private String namePinyin;//姓名拼音
    private String account;//账号
    @TableField(select = false)
    private String password;//密码
    private String phone;//手机号
    private Integer status;//状态
    private Integer identity;//身份
    private Long storeId;//隶属门店ID
    private String createTime;//创建时间
    private String updateTime;//更新时间
}
