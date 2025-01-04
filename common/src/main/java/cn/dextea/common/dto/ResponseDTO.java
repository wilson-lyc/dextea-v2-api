package cn.dextea.common.dto;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

@Data
public class ResponseDTO {
    private int code;
    private String msg;
    private JSONObject data;

    public ResponseDTO(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public ResponseDTO(int code,String msg,JSONObject data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
}
