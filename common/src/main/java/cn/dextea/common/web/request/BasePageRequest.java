package cn.dextea.common.web.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页查询基类，包含 current 和 size 字段。
 * 子类只需定义业务筛选字段。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageRequest {

    @Min(value = 1, message = "当前页码不能小于1")
    private Long current = 1L;

    @Min(value = 1, message = "每页条数不能小于1")
    @Max(value = 100, message = "每页条数不能大于100")
    private Long size = 10L;
}
