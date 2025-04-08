package cn.dextea.order.util;

import cn.dextea.order.mapper.AudioMapper;
import cn.dextea.order.pojo.Audio;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

/**
 * @author Lai Yongchao
 */
@Component
public class AudioUtil {
    @Resource
    private AudioMapper audioMapper;

    public String getAudio(Integer... ids) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        for (Integer id : ids){
            Audio audio=audioMapper.selectById(id);
            if (Objects.isNull(audio)){
                throw new RuntimeException("音频id错误");
            }
            byte[] decodedBytes = Base64.getDecoder().decode(audio.getContent());
            try {
                outputStream.write(decodedBytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        byte[] combinedBytes = outputStream.toByteArray();
        return Base64.getEncoder().encodeToString(combinedBytes);
    }
}
