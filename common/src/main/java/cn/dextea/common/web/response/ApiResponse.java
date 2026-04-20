package cn.dextea.common.web.response;

import cn.dextea.common.code.ResponseCode;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用 API 响应模型
 *
 * @param <T> data 的类型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private Integer code;
    private String msg;
    private T data;

    /**
     * 构建默认成功响应，不返回业务数据。
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 构建默认成功响应，并返回业务数据。
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .msg(ResponseCode.SUCCESS.getMessage())
                .data(data)
                .build();
    }

    /**
     * 判断响应是否成功（code == ResponseCode.SUCCESS.getCode()）。
     * 可在 @Cacheable unless 表达式中使用：unless = "!#result.success"
     */
    @JsonIgnore
    public boolean isSuccess() {
        return ResponseCode.SUCCESS.getCode().equals(this.code);
    }

    /**
     * 构建失败响应。
     */
    public static <T> ApiResponse<T> fail(Integer code, String msg) {
        return ApiResponse.<T>builder()
                .code(code)
                .msg(msg)
                .build();
    }

    /**
     * 构建未登录响应。
     */
    public static <T> ApiResponse<T> unauthorized(String msg) {
        return fail(ResponseCode.UNAUTHORIZED.getCode(), msg);
    }

    /**
     * 构建无权限响应。
     */
    public static <T> ApiResponse<T> forbidden(String msg) {
        return fail(ResponseCode.FORBIDDEN.getCode(), msg);
    }
}
