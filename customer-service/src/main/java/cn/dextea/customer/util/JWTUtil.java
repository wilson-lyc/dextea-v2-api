package cn.dextea.customer.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTValidator;
import cn.hutool.jwt.signers.JWTSignerUtil;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Lai Yongchao
 */
@Component
public class JWTUtil {
    private static final String SECRET_KEY = "w#ils/o+n@de%xta&e.cn";
    public String createToken(Long id) {
        return JWT.create()
                .setPayload("id", id)
                .setExpiresAt(DateUtil.tomorrow())// 过期时间
                .setKey(SECRET_KEY.getBytes())
                .sign();
    }

    public boolean verifyToken(String token) {
        return JWT.of(token).setKey(SECRET_KEY.getBytes()).validate(0);
    }
}
