package cn.dextea.common.dto;

import cn.dextea.common.code.ResponseCode;
import lombok.Data;

/**
 * @author Lai Yongchao
 */
@Data
public class DexteaApiResponse<T> {
    private int code;
    private String message;
    private T data;
    public static <T> DexteaApiResponse<T> success() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> success(String message) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(message);
        return response;
    }
    public static <T> DexteaApiResponse<T> success(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(ResponseCode.SUCCESS.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> success(String message, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SUCCESS.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> DexteaApiResponse<T> fail() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMessage(ResponseCode.FAIL.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> fail(String message) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMessage(message);
        return response;
    }
    public static <T> DexteaApiResponse<T> fail(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMessage(ResponseCode.FAIL.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> fail(String message, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FAIL.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }

    public static <T> DexteaApiResponse<T> unauthorized() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.UNAUTHORIZED.getCode());
        response.setMessage(ResponseCode.UNAUTHORIZED.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> unauthorized(String message) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.UNAUTHORIZED.getCode());
        response.setMessage(message);
        return response;
    }
    public static <T> DexteaApiResponse<T> unauthorized(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.UNAUTHORIZED.getCode());
        response.setMessage(ResponseCode.UNAUTHORIZED.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> unauthorized(String message, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.UNAUTHORIZED.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> forbidden() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FORBIDDEN.getCode());
        response.setMessage(ResponseCode.FORBIDDEN.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> forbidden(String message) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FORBIDDEN.getCode());
        response.setMessage(message);
        return response;
    }
    public static <T> DexteaApiResponse<T> forbidden(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FORBIDDEN.getCode());
        response.setMessage(ResponseCode.FORBIDDEN.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> forbidden(String message, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.FORBIDDEN.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> notFound() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMessage(ResponseCode.NOT_FOUND.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> notFound(String message) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMessage(message);
        return response;
    }
    public static <T> DexteaApiResponse<T> notFound(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMessage(ResponseCode.NOT_FOUND.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> notFound(String message, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.NOT_FOUND.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> serverError() {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SERVER_ERROR.getCode());
        response.setMessage(ResponseCode.SERVER_ERROR.getMessage());
        return response;
    }
    public static <T> DexteaApiResponse<T> serverError(String message) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SERVER_ERROR.getCode());
        response.setMessage(message);
        return response;
    }
    public static <T> DexteaApiResponse<T> serverError(T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SERVER_ERROR.getCode());
        response.setMessage(ResponseCode.SERVER_ERROR.getMessage());
        response.setData(data);
        return response;
    }
    public static <T> DexteaApiResponse<T> serverError(String message, T data) {
        DexteaApiResponse<T> response = new DexteaApiResponse<>();
        response.setCode(ResponseCode.SERVER_ERROR.getCode());
        response.setMessage(message);
        response.setData(data);
        return response;
    }
}
