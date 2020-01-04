package com.xrlj.framework.config;

import com.alibaba.fastjson.JSONObject;
import com.xrlj.framework.spring.mvc.api.ApiException;
import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
@Configuration
public class FeignExceptionConfiguration {

    @Bean
    public ErrorDecoder errorDecoder() {
        return new UserErrorDecoder();
    }

    /**
     * 重新实现feign的异常处理，捕捉restful接口返回的json格式的异常信息
     *
     */
    public class UserErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String s, Response response) {
            Exception exception = null;
            try {
                String json = Util.toString(response.body().asReader());
                exception = new RuntimeException(json);
                if (StringUtils.isEmpty(json)) {
                    return null;
                }
                JSONObject jsonObject = JSONObject.parseObject(json, JSONObject.class);
                int status = jsonObject.getIntValue("status");
                String msg = jsonObject.getString("message");
                if (status != HttpStatus.OK.value()) {
                    exception = new ApiException(status, msg, exception);
                }
            } catch (IOException ex) {
                log.error(ex.getMessage(), ex);
            }
            return exception;
        }
    }
}
