package cn.dextea.staff.dto;

import cn.dextea.common.code.StaffIdentity;
import cn.dextea.common.code.StaffStatus;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@RequiredArgsConstructor
public class StaffDTO {
    Long id;//员工ID
    String name;//姓名
    String namePinyin;//姓名拼音
    String account;//账号
    String phone;//手机号
    Integer status;//状态 0-禁用 1-启用
    Integer identity;//身份
    Long storeId;//隶属门店ID
    String storeName;//门店名称
    String createTime;//创建时间
    String updateTime;//更新时间
}
