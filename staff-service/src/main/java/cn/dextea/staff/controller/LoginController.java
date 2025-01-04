package cn.dextea.staff.controller;

import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {
    /**
     * 密码登录
     */
    @GetMapping("/pwd")
    public JSONObject loginByPwd() {
        JSONObject res=new JSONObject();
        res.put("code",200);
        res.put("msg","登录成功");
        return res;
    }
}
