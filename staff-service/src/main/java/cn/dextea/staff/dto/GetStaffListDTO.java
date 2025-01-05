package cn.dextea.staff.dto;

import jakarta.validation.constraints.Min;
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
public class GetStaffListDTO {
    @NotNull(message = "当前页不能为空")
    @Min(value = 1, message = "当前页不能小于1")
    int currentPage;
    @NotNull
    @Min(value = 1, message = "页大小不能小于1")
    int pageSize;
}
