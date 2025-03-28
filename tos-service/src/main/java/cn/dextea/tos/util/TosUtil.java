package cn.dextea.tos.util;

import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.tos.model.object.DeleteObjectInput;
import com.volcengine.tos.model.object.PutObjectInput;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Slf4j
@Component
@RefreshScope
public class TosUtil {
    @Value("${tos.access_key}")
    private String ACCESS_KEY;
    @Value("${tos.secret_key}")
    private String SECRET_KEY;
    private final String BUCKET_NAME = "dextea";
    private final String BASE_URL = "https://dextea.tos-cn-guangzhou.volces.com";
    private TOSV2 tos;

    @PostConstruct  // 替代构造函数初始化
    public void init() {
        if (ACCESS_KEY == null || SECRET_KEY == null) {
            throw new IllegalStateException("TOS 密钥未正确配置");
        }
        String ENDPOINT = "tos-cn-guangzhou.volces.com";
        String REGION = "cn-guangzhou";
        this.tos = new TOSV2ClientBuilder().build(REGION, ENDPOINT, ACCESS_KEY, SECRET_KEY);
        log.info("TOS客户端初始化完成");
    }

    public String uploadMultipartFile(String folder,String fileName, MultipartFile file){
        String timestamp = String.valueOf(Instant.now().toEpochMilli()); // 获取当前时间戳
        String key = String.format("%s/%s_%s%s", folder,fileName, timestamp,getFileExtension(file));
        return uploadFileStream(key,file);
    }

    private String uploadFileStream(String key, MultipartFile file){
        try {
            InputStream inputStream = file.getInputStream();
            PutObjectInput putObjectInput = new PutObjectInput().setBucket(BUCKET_NAME).setKey(key).setContent(inputStream);
            tos.putObject(putObjectInput);
            return String.format("%s/%s", BASE_URL, key);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取文件后缀名
     * @param file 文件
     */
    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "";
    }

    public boolean delete(String url){
        String key = url.replace(BASE_URL + "/", "");
        DeleteObjectInput input=new DeleteObjectInput().setBucket(BUCKET_NAME).setKey(key);
        tos.deleteObject(input);
        return true;
    }
}
