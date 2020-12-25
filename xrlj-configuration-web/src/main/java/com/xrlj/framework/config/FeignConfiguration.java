package com.xrlj.framework.config;

import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * 放在扫描包下。全局有效。所有feign客户端有效。
 * 配合自定义熔断策略。
 * @see FeignHystrixConcurrencyStrategy
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
            //配置自定义熔断策略或者在.yml中配置熔断策略为hystrix.command.default.execution.isolation.strategy: SEMAPHORE
            //否则这里返回null
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return;
            }
            HttpServletRequest request = attributes.getRequest();

            //添加所有头信息。feign调用，在各组件中传递。保持各组件之间session一致性，同一个sessionId。
            Enumeration<String> headerNames = request.getHeaderNames();
            if (headerNames != null) {
                while (headerNames.hasMoreElements()) {
                    String name = headerNames.nextElement();
                    Enumeration<String> values = request.getHeaders(name);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        // 跳过 content-length
                        // https://blog.csdn.net/qq_39986681/article/details/107138740
                        // https://juejin.cn/post/6844903939079421966
                        if (name.equals("content-length")){
                            continue;
                        }
                        template.header(name, value);
                    }
                }
            }
        }
    }
}
