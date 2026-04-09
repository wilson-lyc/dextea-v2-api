package cn.dextea.common.code;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 全局业务错误码，用于非 HTTP 标准错误场景的细粒度错误描述。
 * 命名格式：{错误类型}_ERROR。
 * <p>
 * Code 区间规划：
 * <ul>
 *   <li>40001-40099：参数与校验类错误</li>
 *   <li>40101-40199：认证与鉴权类错误</li>
 *   <li>40301-40399：权限类错误</li>
 *   <li>40401-40499：资源类错误</li>
 *   <li>50001-50099：服务器与数据库类错误</li>
 *   <li>50301-50399：第三方服务类错误</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum GlobalErrorCode {

    // ========== 参数与校验类 (40001-40099) ==========

    /**
     * 请求参数错误。
     */
    PARAM_ERROR(40001, "请求参数错误"),

    /**
     * 请求体解析失败。
     */
    INVALID_REQUEST_BODY(40002, "请求体格式错误"),

    /**
     * 请求方法不支持。
     */
    METHOD_NOT_ALLOWED(40003, "请求方法不支持"),

    /**
     * 数据格式错误。
     */
    INVALID_FORMAT(40004, "数据格式错误"),

    /**
     * 数据长度超限。
     */
    DATA_TOO_LONG(40005, "数据长度超限"),

    // ========== 认证与鉴权类 (40101-40199) ==========

    /**
     * token 无效或已过期。
     */
    TOKEN_INVALID(40101, "登录凭证无效或已过期"),

    /**
     * token 解析失败。
     */
    TOKEN_PARSE_FAILED(40102, "登录凭证解析失败"),

    /**
     * 用户不存在。
     */
    USER_NOT_FOUND(40103, "用户不存在"),

    /**
     * 账号已被禁用。
     */
    ACCOUNT_DISABLED(40104, "账号已被禁用"),

    // ========== 权限类 (40301-40399) ==========

    /**
     * 权限不足。
     */
    PERMISSION_DENIED(40301, "权限不足"),

    // ========== 资源类 (40401-40499) ==========

    /**
     * 资源不存在。
     */
    RESOURCE_NOT_FOUND(40401, "资源不存在"),

    // ========== 服务器与数据库类 (50001-50099) ==========

    /**
     * 数据库异常。
     */
    DATABASE_ERROR(50001, "数据库异常"),

    /**
     * 数据库连接失败。
     */
    DB_CONNECTION_FAILED(50002, "数据库连接失败"),

    /**
     * 服务器内部错误。
     */
    INTERNAL_SERVER_ERROR(50003, "服务器内部错误"),

    /**
     * 文件操作失败。
     */
    FILE_OPERATION_FAILED(50004, "文件操作失败"),

    // ========== 第三方服务类 (50301-50399) ==========

    /**
     * 第三方服务不可用。
     */
    EXTERNAL_SERVICE_UNAVAILABLE(50301, "第三方服务暂不可用"),

    /**
     * 第三方服务调用超时。
     */
    EXTERNAL_SERVICE_TIMEOUT(50302, "第三方服务调用超时");

    private final Integer code;
    private final String msg;
}
