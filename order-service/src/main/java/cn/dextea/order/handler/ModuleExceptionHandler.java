package cn.dextea.order.handler;

import cn.dextea.common.model.common.DexteaApiResponse;
import com.alipay.api.AlipayApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ModuleExceptionHandler {
    @ExceptionHandler(AlipayApiException.class)
    public DexteaApiResponse<Void> handleAlipayApiException(AlipayApiException e) {
        log.error("支付宝接口异常");
        return DexteaApiResponse.serverError("支付服务异常");
    }
}