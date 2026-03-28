package cn.dextea.staff.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindStaffStoreRequest {
    @NotNull(message = "门店ID是必填的")
    private Long storeId;
}
