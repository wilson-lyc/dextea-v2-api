package cn.dextea.common.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;

/**
 * @author Lai Yongchao
 */
public class DexteaJWTUtil {
    private final String SECRET_KEY;

    public DexteaJWTUtil(String SECRET_KEY) {
        this.SECRET_KEY = SECRET_KEY;
    }
    public String createToken(Long id) {
        return JWT.create()
                .setPayload("id", id)
                .setExpiresAt(DateUtil.tomorrow())// 过期时间
                .setKey(SECRET_KEY.getBytes())
                .sign();
    }
    public boolean verifyToken(String token) {
        try{
            return JWT.of(token).setKey(SECRET_KEY.getBytes()).validate(0);
        }catch (Exception e){
            return false;
        }
    }

    public Long getCustomerId(String token){
        String idStr=JWT.of(token).setKey(SECRET_KEY.getBytes()).getPayload("id").toString();
        return Long.valueOf(idStr);
    }
}
