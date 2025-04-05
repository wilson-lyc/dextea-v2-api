package cn.dextea.store.dto;

import cn.dextea.common.code.StoreStatus;
import cn.dextea.store.pojo.Store;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreCreateDTO {
    @NotBlank(message = "门店名称不能为空")
    private String name;
    @NotBlank(message = "省份不能为空")
    private String province;
    @NotBlank(message = "城市不能为空")
    private String city;
    @NotBlank(message = "区县不能为空")
    private String district;
    @NotBlank(message = "详细地址不能为空")
    private String address;
    @NotBlank(message = "联系人不能为空")
    private String linkman;
    @NotBlank(message = "联系电话不能为空")
    private String phone;
    @NotBlank(message = "营业时间不能为空")
    private String openTime;
    public Store toStore(){
        return Store.builder()
                .name(name)
                .province(province)
                .city(city)
                .district(district)
                .address(address)
                .linkman(linkman)
                .phone(phone)
                .openTime(openTime)
                .status(StoreStatus.NOT_ACTIVE.getValue())
                .build();
    }
}