package cn.dextea.store.dto;

import cn.dextea.store.pojo.Store;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateStoreDTO {
    @NotBlank
    private String name;
    @NotBlank
    private String province;
    @NotBlank
    private String city;
    @NotBlank
    private String district;
    @NotBlank
    private String address;
    @NotBlank
    private String linkman;
    @NotBlank
    private String phone;
    @NotBlank
    private String openTime;
    @NotNull
    private Integer status;
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
                .status(status)
                .build();
    }
}