package cn.dextea.customer.service.impl;

import cn.dextea.common.web.response.ApiResponse;
import cn.dextea.customer.enums.CustomerErrorCode;
import cn.dextea.customer.enums.EmailCodeScene;
import cn.dextea.customer.service.EmailCodeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱验证码服务实现。
 * 验证码有效期 5 分钟，同一邮箱同一场景 60 秒内不允许重复发送。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailCodeServiceImpl implements EmailCodeService {

    private static final int CODE_LENGTH = 6;
    private static final long CODE_TTL_SECONDS = 300L;       // 5 分钟
    private static final long RATE_LIMIT_SECONDS = 60L;      // 60 秒限流

    /** Redis key: customer:email:code:{scene}:{email} */
    private static final String CODE_KEY_PREFIX = "customer:email:code:";
    /** Redis key: customer:email:rate:{scene}:{email} */
    private static final String RATE_KEY_PREFIX = "customer:email:rate:";

    private final StringRedisTemplate stringRedisTemplate;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromAddress;

    @Override
    public ApiResponse<Void> sendCode(String email, EmailCodeScene scene) {
        String rateKey = RATE_KEY_PREFIX + scene.name() + ":" + email;
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(rateKey))) {
            return fail(CustomerErrorCode.EMAIL_CODE_SEND_TOO_FREQUENT);
        }

        String code = generateCode();
        String codeKey = CODE_KEY_PREFIX + scene.name() + ":" + email;

        try {
            doSendMail(email, scene, code);
        } catch (Exception e) {
            log.error("发送验证码邮件失败, email={}, scene={}", email, scene, e);
            return fail(CustomerErrorCode.EMAIL_CODE_SEND_FAILED);
        }

        // 邮件发送成功后再写 Redis，避免发送失败时留下脏数据。
        stringRedisTemplate.opsForValue().set(codeKey, code, CODE_TTL_SECONDS, TimeUnit.SECONDS);
        stringRedisTemplate.opsForValue().set(rateKey, "1", RATE_LIMIT_SECONDS, TimeUnit.SECONDS);

        return ApiResponse.success();
    }

    @Override
    public boolean verifyCode(String email, EmailCodeScene scene, String code) {
        String codeKey = CODE_KEY_PREFIX + scene.name() + ":" + email;
        String stored = stringRedisTemplate.opsForValue().get(codeKey);
        if (stored == null || !stored.equals(code)) {
            return false;
        }
        // 验证通过，立即删除，防止重复使用。
        stringRedisTemplate.delete(codeKey);
        return true;
    }

    private void doSendMail(String email, EmailCodeScene scene, String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(email);
        message.setSubject(buildSubject(scene));
        message.setText(buildBody(scene, code));
        mailSender.send(message);
    }

    private String buildSubject(EmailCodeScene scene) {
        return switch (scene) {
            case REGISTER -> "【Dextea】邮箱验证码 - 注册";
            case LOGIN -> "【Dextea】邮箱验证码 - 登录";
            case RESET_PASSWORD -> "【Dextea】邮箱验证码 - 重置密码";
        };
    }

    private String buildBody(EmailCodeScene scene, String code) {
        String action = switch (scene) {
            case REGISTER -> "注册";
            case LOGIN -> "登录";
            case RESET_PASSWORD -> "重置密码";
        };
        return String.format(
                "您正在进行【%s】操作，验证码为：%s\n\n验证码有效期 5 分钟，请勿泄露给他人。\n\n如非本人操作，请忽略此邮件。",
                action, code);
    }

    private String generateCode() {
        SecureRandom random = new SecureRandom();
        int num = random.nextInt(900000) + 100000; // 100000 ~ 999999
        return String.valueOf(num);
    }

    private <T> ApiResponse<T> fail(CustomerErrorCode errorCode) {
        return ApiResponse.fail(errorCode.getCode(), errorCode.getMsg());
    }
}
