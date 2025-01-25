package cn.dextea.tos.util;

import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.tos.TosClientException;
import com.volcengine.tos.TosServerException;
import com.volcengine.tos.model.object.PutObjectInput;
import com.volcengine.tos.model.object.PutObjectOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

@Slf4j
@Component
public class TosUtil {
    private final String ENDPOINT = "tos-cn-guangzhou.volces.com";
    private final String REGION = "cn-guangzhou";
    private final String BUCKET_NAME = "dextea";
    private final String ACCESS_KEY= System.getenv("TOS_ACCESS_KEY");
    private final String SECRET_KEY= System.getenv("TOS_SECRET_KEY");
    private final String BASE_URL = "https://dextea.tos-cn-guangzhou.volces.com";
    private TOSV2 tos;

    public TosUtil() {
        this.tos=new TOSV2ClientBuilder().build(REGION, ENDPOINT,ACCESS_KEY, SECRET_KEY);
    }

    public String uploadMultipartFile(String folder,MultipartFile file) {
        try {
            String originalFilename = file.getOriginalFilename(); // 获取文件原名
            String timestamp = String.valueOf(Instant.now().toEpochMilli()); // 获取当前时间戳
            String key = String.format("%s/%s-%s", folder, timestamp, originalFilename); // 拼接文件名
            InputStream inputStream=file.getInputStream();
            PutObjectInput putObjectInput = new PutObjectInput().setBucket(BUCKET_NAME).setKey(key).setContent(inputStream);
            tos.putObject(putObjectInput);
            return String.format("%s/%s", BASE_URL,key);
        } catch (IOException e) {
            log.error("上传文件时，getInputStream失败", e);
            return null;
        } catch (TosClientException e) {
            // 操作失败，捕获客户端异常，一般情况是请求参数错误，此时请求并未发送
            log.error("TOS客户端异常: {}", e.getMessage());
            if (e.getCause() != null) {
                e.getCause().printStackTrace();
            }
            return null;
        } catch (TosServerException e) {
            // 操作失败，捕获服务端异常，可以获取到从服务端返回的详细错误信息
            log.error("TOS服务端异常: StatusCode={}, Code={}, Message={}, RequestID={}",e.getStatusCode(),e.getCode(),e.getMessage(),e.getRequestID());
            return null;
        } catch (Throwable t) {
            // 作为兜底捕获其他异常，一般不会执行到这里
            log.error("putObject failed");
            log.error("unexpected exception, message: {}", t.getMessage());
            return null;
        }
    }
}
