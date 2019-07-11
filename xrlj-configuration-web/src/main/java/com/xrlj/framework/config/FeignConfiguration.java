package com.xrlj.framework.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 放在扫描包下。全局有效。所有feign客户端有效。
 */
@Configuration
public class FeignConfiguration {

    /**
     * 日志级别。FULL打印请求头，请求体等信息。
     * @return
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

    /**
     * 创建Feign请求拦截器，在发送请求前设置认证的token,各个微服务将token设置到环境变量中来达到通用
     * @return
     * */
    @Bean
    public FeignBasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new FeignBasicAuthRequestInterceptor();
    }

    /**
     * Feign请求拦截器
     * @author yinjihuan
     * @create 2017-11-10 17:25
     **/
    public class FeignBasicAuthRequestInterceptor  implements RequestInterceptor {

        public FeignBasicAuthRequestInterceptor() {

        }

        @Override
        public void apply(RequestTemplate template) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            //api版本信息
            String apiVersion = request.getHeader("Content-Version");
            if (!StringUtils.isEmpty(apiVersion)) {
                template.header("Content-Version", apiVersion);
            }

            //获取客户端传过来token
            String authorization = request.getHeader("Authorization");
            String token = StringUtils.removeStart(authorization, "Bearer ");
            if (!StringUtils.isEmpty(token)) {
                //添加token
                template.header("Authorization", "Bearer ".concat(token));
            }
        }
    }
}
