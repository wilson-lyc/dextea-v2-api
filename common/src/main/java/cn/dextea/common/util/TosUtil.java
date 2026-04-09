package cn.dextea.common.util;

import cn.dextea.common.exception.TosUtilException;
import com.volcengine.tos.TOSV2;
import com.volcengine.tos.TOSV2ClientBuilder;
import com.volcengine.tos.TosClientException;
import com.volcengine.tos.TosServerException;
import com.volcengine.tos.model.object.PutObjectInput;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;

/**
 * @author Lai Yongchao
 */
public class TosUtil{
    private static final String BUCKET_NAME = "dextea";
    private static final String BASE_URL = "https://dextea.tos-cn-guangzhou.volces.com";
    private static final String ENDPOINT = "tos-cn-guangzhou.volces.com";
    private static final String REGION = "cn-guangzhou";
    private TOSV2 tos;

    public TosUtil(String ACCESS_KEY, String SECRET_KEY) {
        this.tos = new TOSV2ClientBuilder().build(REGION, ENDPOINT, ACCESS_KEY, SECRET_KEY);
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


    /**
     * 上传文件
     * @param folder 文件夹
     * @param fileName 文件名
     * @param file 文件
     * @return 访问url
     */
    public String uploadFile(String folder,String fileName, MultipartFile file) throws TosUtilException {
        // 预处理文件夹名 - 删除开头的“/”
        if(folder.startsWith("/"))
            folder = folder.substring(1);
        // 获取当前时间戳
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        // 生成上传key - folder/fileName_timestamp.fileExtension
        String key = String.format("%s/%s_%s%s", folder,fileName, timestamp,getFileExtension(file));
        try {
            InputStream inputStream = file.getInputStream();
            PutObjectInput putObjectInput = new PutObjectInput().setBucket(BUCKET_NAME).setKey(key).setContent(inputStream);
            tos.putObject(putObjectInput);
            return String.format("%s/%s", BASE_URL, key);
        } catch (IOException e) {
            throw new TosUtilException("IO异常",e);
        } catch (TosClientException e) {
            throw new TosUtilException("Tos客户端异常",e);
        } catch (TosServerException e) {
            throw new TosUtilException("Tos服务端异常",e);
        }
    }
}
