package cn.dextea.staff.pojo;

import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("s_staff")
public class Staff {
    @TableId(type = IdType.AUTO)
    Long id;// 员工id
    String name;// 姓名
    String namePinyin;// 姓名拼音
    String account;// 账号
    @TableField(select = false)
    String password;// 密码
    String phone;// 电话
    String createTime;//创建时间
    String updateTime;// 更新时间
    Boolean status;// 状态
    Integer side;// 端侧 1-company 2-store
    Integer storeId;// 门店id
    @TableField(exist = false)
    String storeName;
}
