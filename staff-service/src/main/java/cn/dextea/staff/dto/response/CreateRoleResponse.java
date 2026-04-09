package cn.dextea.staff.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoleResponse {
    private Long id;

    private String name;

    private String remark;

    private Integer dataScope;

    private Integer status;

    private LocalDateTime createTime;
}
