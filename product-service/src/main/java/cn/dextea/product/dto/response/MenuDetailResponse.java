package cn.dextea.product.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuDetailResponse {
    private Long id;
    private String name;
    private String description;
    private Integer status;
    private List<MenuGroupResponse> groups;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
