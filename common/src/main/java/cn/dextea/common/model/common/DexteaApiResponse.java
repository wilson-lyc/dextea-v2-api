package cn.dextea.common.model.common;

import cn.dextea.common.code.ResponseCode;
import lombok.Data;

/**
 * @author Lai Yongchao
 */
@Data
public class DexteaApiResponse<T> {
    private int code;
    private String msg;
    private String subCode;
    private String subMsg;
    private T data;
    public static <T> DexteaApiResponse<T> success() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMsg(ResponseCode.SUCCESS.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> success(String msg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMsg(msg);
        return response;
    }
    public static <T> DexteaApiResponse<T> success(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMsg(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> success(String msg, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMsg(msg);
        response.setData(data);
        return response;
    }

    public static <T> DexteaApiResponse<T> fail() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMsg(ResponseCode.FAIL.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> fail(String msg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMsg(msg);
        return response;
    }
    public static <T> DexteaApiResponse<T> fail(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMsg(ResponseCode.FAIL.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> fail(String msg, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMsg(msg);
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> fail(String subCode,String subMsg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMsg(ResponseCode.FAIL.getMessage());
        response.setSubCode(subCode);
        response.setSubMsg(subMsg);
        return response;
    }

    public static <T> DexteaApiResponse<T> fail(String msg, String subCode,String subMsg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMsg(msg);
        response.setSubCode(subCode);
        response.setSubMsg(subMsg);
        return response;
    }

    public static <T> DexteaApiResponse<T> unauthorized() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.UNAUTHORIZED.getCode());
        response.setMsg(ResponseCode.UNAUTHORIZED.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> unauthorized(String msg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.UNAUTHORIZED.getCode());
        response.setMsg(msg);
        return response;
    }
    public static <T> DexteaApiResponse<T> unauthorized(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.UNAUTHORIZED.getCode());
        response.setMsg(ResponseCode.UNAUTHORIZED.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> unauthorized(String msg, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.UNAUTHORIZED.getCode());
        response.setMsg(msg);
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> forbidden() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FORBIDDEN.getCode());
        response.setMsg(ResponseCode.FORBIDDEN.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> forbidden(String msg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FORBIDDEN.getCode());
        response.setMsg(msg);
        return response;
    }
    public static <T> DexteaApiResponse<T> forbidden(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FORBIDDEN.getCode());
        response.setMsg(ResponseCode.FORBIDDEN.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> forbidden(String msg, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FORBIDDEN.getCode());
        response.setMsg(msg);
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> notFound() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMsg(ResponseCode.NOT_FOUND.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> notFound(String msg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMsg(msg);
        return response;
    }
    public static <T> DexteaApiResponse<T> notFound(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMsg(ResponseCode.NOT_FOUND.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> notFound(String msg, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMsg(msg);
        response.setData(data);
        return response;
    }

    public static <T> DexteaApiResponse<T> notFound(String subCode,String subMsg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMsg(ResponseCode.NOT_FOUND.getMessage());
        response.setSubCode(subCode);
        response.setSubMsg(subMsg);
        return response;
    }

    public static <T> DexteaApiResponse<T> serverError() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SERVER_ERROR.getCode());
        response.setMsg(ResponseCode.SERVER_ERROR.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> serverError(String msg) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SERVER_ERROR.getCode());
        response.setMsg(msg);
        return response;
    }
    public static <T> DexteaApiResponse<T> serverError(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SERVER_ERROR.getCode());
        response.setMsg(ResponseCode.SERVER_ERROR.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> serverError(String msg, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SERVER_ERROR.getCode());
        response.setMsg(msg);
        response.setData(data);
        return response;
    }
}
