package cn.dextea.common.model.common;

import cn.dextea.common.code.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private int code;
    private String message;
    private Map<String, Object> data;
    // 200 - SUCCESS
    public static ApiResponse success(){
        return ApiResponse.builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMsg())
                .build();
    }
    public static ApiResponse success(String msg){
        return ApiResponse.builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(msg)
                .build();
    }
    public static ApiResponse success(Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(ResultCode.SUCCESS.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse success(String msg,Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.SUCCESS.getCode())
                .message(msg)
                .data(data)
                .build();
    }
    // 400 - BAD_REQUEST
    public static ApiResponse badRequest(){
        return ApiResponse.builder()
                .code(ResultCode.BAD_REQUEST.getCode())
                .message(ResultCode.BAD_REQUEST.getMsg())
                .build();
    }
    public static ApiResponse badRequest(String msg){
        return ApiResponse.builder()
                .code(ResultCode.BAD_REQUEST.getCode())
                .message(msg)
                .build();
    }
    public static ApiResponse badRequest(Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.BAD_REQUEST.getCode())
                .message(ResultCode.BAD_REQUEST.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse badRequest(String msg,Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.BAD_REQUEST.getCode())
                .message(msg)
                .data(data)
                .build();
    }
    // 500 - SERVER_ERROR
    public static ApiResponse serverError(){
        return ApiResponse.builder()
                .code(ResultCode.SERVER_ERROR.getCode())
                .message(ResultCode.SERVER_ERROR.getMsg())
                .build();
    }
    public static ApiResponse serverError(String msg){
        return ApiResponse.builder()
                .code(ResultCode.SERVER_ERROR.getCode())
                .message(msg)
                .build();
    }
    public static ApiResponse serverError(Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.SERVER_ERROR.getCode())
                .message(ResultCode.SERVER_ERROR.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse serverError(String msg,Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.SERVER_ERROR.getCode())
                .message(msg)
                .data(data)
                .build();
    }
    // 404 - NOT_FOUND
    public static ApiResponse notFound(){
        return ApiResponse.builder()
                .code(ResultCode.NOT_FOUND.getCode())
                .message(ResultCode.NOT_FOUND.getMsg())
                .build();
    }
    public static ApiResponse notFound(String msg){
        return ApiResponse.builder()
                .code(ResultCode.NOT_FOUND.getCode())
                .message(msg)
                .build();
    }
    public static ApiResponse notFound(Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.NOT_FOUND.getCode())
                .message(ResultCode.NOT_FOUND.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse notFound(String msg,Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.NOT_FOUND.getCode())
                .message(msg)
                .data(data)
                .build();
    }

    // 403 - FORBIDDEN
    public static ApiResponse forbidden(){
        return ApiResponse.builder()
                .code(ResultCode.FORBIDDEN.getCode())
                .message(ResultCode.FORBIDDEN.getMsg())
                .build();
    }
    public static ApiResponse forbidden(String msg){
        return ApiResponse.builder()
                .code(ResultCode.FORBIDDEN.getCode())
                .message(msg)
                .build();
    }
    public static ApiResponse forbidden(Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.FORBIDDEN.getCode())
                .message(ResultCode.FORBIDDEN.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse forbidden(String msg,Map<String, Object> data){
        return ApiResponse.builder()
                .code(ResultCode.FORBIDDEN.getCode())
                .message(msg)
                .data(data)
                .build();
    }
}
