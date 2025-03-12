package cn.dextea.tos.util;

import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.tos.model.object.DeleteObjectInput;
import com.volcengine.tos.model.object.PutObjectInput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Slf4j
@Component
public class TosUtil {
    private final String BUCKET_NAME = "dextea";
    private final String BASE_URL = "https://dextea.tos-cn-guangzhou.volces.com";
    private final TOSV2 tos;

    public TosUtil() {
        String ENDPOINT = "tos-cn-guangzhou.volces.com";
        String REGION = "cn-guangzhou";
        String ACCESS_KEY = System.getenv("TOS_ACCESS_KEY");
        String SECRET_KEY = System.getenv("TOS_SECRET_KEY");
        this.tos=new TOSV2ClientBuilder().build(REGION, ENDPOINT, ACCESS_KEY, SECRET_KEY);
    }

    public String uploadMultipartFile(String folder, MultipartFile file){
        String originalFilename = file.getOriginalFilename(); // 获取文件原名
        String timestamp = String.valueOf(Instant.now().toEpochMilli()); // 获取当前时间戳
        String key = String.format("%s/%s_%s", folder, timestamp, originalFilename); // 拼接文件名
        return uploadFileStream(key,file);
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

    private String getFileExtension(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && originalFilename.contains(".")) {
            return originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "";
    }

    public Boolean delete(String url){
        String key = url.replace(BASE_URL + "/", "");
        DeleteObjectInput input=new DeleteObjectInput().setBucket(BUCKET_NAME).setKey(key);
        tos.deleteObject(input);
        return true;
    }
}
