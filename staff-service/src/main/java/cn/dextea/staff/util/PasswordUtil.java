package cn.dextea.staff.util;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class PasswordUtil {
    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();
    private static final CharacterData PUNCTUATION = new CharacterData() {
        @Override
        public String getErrorCode() {
            return "INSUFFICIENT_PUNCTUATION";
        }
        @Override
        public String getCharacters() {
            return "!@#$%&*()_+";
        }
    };

    /**
     * 生成密码
     * @return 密码
     */
    public String generate(){
        List<CharacterRule> characterRuleList = Arrays.asList(
                new CharacterRule(EnglishCharacterData.UpperCase, 2),
                new CharacterRule(EnglishCharacterData.LowerCase, 2),
                new CharacterRule(EnglishCharacterData.Digit, 2),
                new CharacterRule(PUNCTUATION, 1)
        );
        PasswordGenerator generator = new PasswordGenerator();
        return generator.generatePassword(10, characterRuleList);
    }

    /**
     * 加密密码
     * @param password 密码
     * @return 加密后的密码
     */
    public String encode(String password){
        return PASSWORD_ENCODER.encode(password);
    }

    /**
     * 校验密码
     * @param rawPassword 明文密码
     * @param encodedPassword 加密后的密码
     * @return 是否匹配
     */
    public boolean matches(String rawPassword, String encodedPassword){
        return PASSWORD_ENCODER.matches(rawPassword, encodedPassword);
    }
}
