package cn.dextea.store.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Lai Yongchao
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreLicenseDTO {
    private Long id;
    private String businessLicense;
    private String foodBusinessLicense;
}
