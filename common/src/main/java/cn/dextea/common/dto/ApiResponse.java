package cn.dextea.common.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @author Lai Yongchao
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse {
    private int code;
    private String msg;
    private JSONObject data;
    // 200 - SUCCESS
    public static ApiResponse success(){
        return ApiResponse.builder()
                .code(ResultCode.SUCCESS.getCode())
                .msg(ResultCode.SUCCESS.getMsg())
                .build();
    }
    public static ApiResponse success(String msg){
        return ApiResponse.builder()
                .code(ResultCode.SUCCESS.getCode())
                .msg(msg)
                .build();
    }
    public static ApiResponse success(JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.SUCCESS.getCode())
                .msg(ResultCode.SUCCESS.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse success(String msg,JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.SUCCESS.getCode())
                .msg(msg)
                .data(data)
                .build();
    }
    // 400 - BAD_REQUEST
    public static ApiResponse badRequest(){
        return ApiResponse.builder()
                .code(ResultCode.BAD_REQUEST.getCode())
                .msg(ResultCode.BAD_REQUEST.getMsg())
                .build();
    }
    public static ApiResponse badRequest(String msg){
        return ApiResponse.builder()
                .code(ResultCode.BAD_REQUEST.getCode())
                .msg(msg)
                .build();
    }
    public static ApiResponse badRequest(JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.BAD_REQUEST.getCode())
                .msg(ResultCode.BAD_REQUEST.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse badRequest(String msg,JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.BAD_REQUEST.getCode())
                .msg(msg)
                .data(data)
                .build();
    }
    // 500 - SERVER_ERROR
    public static ApiResponse serverError(){
        return ApiResponse.builder()
                .code(ResultCode.SERVER_ERROR.getCode())
                .msg(ResultCode.SERVER_ERROR.getMsg())
                .build();
    }
    public static ApiResponse serverError(String msg){
        return ApiResponse.builder()
                .code(ResultCode.SERVER_ERROR.getCode())
                .msg(msg)
                .build();
    }
    public static ApiResponse serverError(JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.SERVER_ERROR.getCode())
                .msg(ResultCode.SERVER_ERROR.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse serverError(String msg,JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.SERVER_ERROR.getCode())
                .msg(msg)
                .data(data)
                .build();
    }
    // 404 - NOT_FOUND
    public static ApiResponse notFound(){
        return ApiResponse.builder()
                .code(ResultCode.NOT_FOUND.getCode())
                .msg(ResultCode.NOT_FOUND.getMsg())
                .build();
    }
    public static ApiResponse notFound(String msg){
        return ApiResponse.builder()
                .code(ResultCode.NOT_FOUND.getCode())
                .msg(msg)
                .build();
    }
    public static ApiResponse notFound(JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.NOT_FOUND.getCode())
                .msg(ResultCode.NOT_FOUND.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse notFound(String msg,JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.NOT_FOUND.getCode())
                .msg(msg)
                .data(data)
                .build();
    }

    // 403 - FORBIDDEN
    public static ApiResponse forbidden(){
        return ApiResponse.builder()
                .code(ResultCode.FORBIDDEN.getCode())
                .msg(ResultCode.FORBIDDEN.getMsg())
                .build();
    }
    public static ApiResponse forbidden(String msg){
        return ApiResponse.builder()
                .code(ResultCode.FORBIDDEN.getCode())
                .msg(msg)
                .build();
    }
    public static ApiResponse forbidden(JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.FORBIDDEN.getCode())
                .msg(ResultCode.FORBIDDEN.getMsg())
                .data(data)
                .build();
    }
    public static ApiResponse forbidden(String msg,JSONObject data){
        return ApiResponse.builder()
                .code(ResultCode.FORBIDDEN.getCode())
                .msg(msg)
                .data(data)
                .build();
    }
}
