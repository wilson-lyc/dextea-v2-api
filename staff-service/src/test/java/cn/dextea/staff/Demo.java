package cn.dextea.staff;

import cn.dextea.staff.util.PasswordUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.crypto.BCUtil;
import cn.hutool.crypto.KeyUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.SM4;
import org.bouncycastle.crypto.engines.SM2Engine;
import org.bouncycastle.crypto.signers.PlainDSAEncoding;
import org.bouncycastle.jcajce.provider.asymmetric.ec.BCECPublicKey;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Lai Yongchao
 */
@SpringBootTest
class Demo {
    @Test
    public void createSm2KeyTest() {
        // 密钥
        byte[] key = KeyUtil.generateKey(SM4.ALGORITHM_NAME, 128).getEncoded();
        System.out.println("密钥: " + HexUtil.encodeHexStr(key));
        String content="hello";
        SM4 sm4 = SmUtil.sm4(key);
        String encryptHex = sm4.encryptHex(content);
        System.out.println("加密后: " + encryptHex);
        String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        System.out.println("解密后: " + decryptStr);
    }
}
