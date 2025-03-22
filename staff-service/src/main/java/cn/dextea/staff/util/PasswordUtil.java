package cn.dextea.staff.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SM4;
import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.stereotype.Component;



import java.util.Arrays;
import java.util.List;

@Component
public class PasswordUtil {

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
    public String create(){
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
    public String encrypt(String password){
        // 从环境变量读取密钥
        String key=System.getenv("secret_key");
        SM4 sm4 = SmUtil.sm4(HexUtil.decodeHex(key));
        return sm4.encryptHex(password);
    }

    /**
     * 解密密码
     * @param password 加密后的密码
     * @return 密码
     */
    public String decrypt(String password){
        // 从环境变量读取密钥
        String key=System.getenv("secret_key");
        SM4 sm4 = SmUtil.sm4(HexUtil.decodeHex(key));
        return sm4.decryptStr(password);
    }
}
