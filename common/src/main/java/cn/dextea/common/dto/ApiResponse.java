package cn.dextea.common.dto;

import lombok.Data;

/**
 * @author Lai Yongchao
 */
@Data
public class ApiResponse {
    private int code;
    private String msg;
    private Object data;

    public ApiResponse(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ApiResponse(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    public static ApiResponse notFound(String msg){
        return new ApiResponse(404,msg);
    }
    public static ApiResponse badRequest(String msg){
        return new ApiResponse(400,msg);
    }
    public static ApiResponse serverError(String msg){
        return new ApiResponse(500,msg);
    }
    public static ApiResponse success(){
        return new ApiResponse(200,"success");
    }
    public static ApiResponse success(Object data){
        return new ApiResponse(200,"success",data);
    }
    public static ApiResponse success(String msg,Object data){
        return new ApiResponse(200,msg,data);
    }
}
