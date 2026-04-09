package cn.dextea.customer.service;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.enums.EmailCodeScene;

public interface EmailCodeService {

    /**
     * 发送验证码到指定邮箱
     * @param email 目标邮箱
     * @param scene 验证码场景
     * @return 发送结果
     */
    ApiResponse<Void> sendCode(String email, EmailCodeScene scene);

    /**
     * 校验验证码。校验通过后验证码立即失效（一次性使用）。
     * @param email 邮箱
     * @param scene 验证码场景
     * @param code  用户输入的验证码
     * @return 是否校验通过
     */
    boolean verifyCode(String email, EmailCodeScene scene, String code);
}
