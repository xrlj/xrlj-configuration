package com.xrlj.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

/**
 * 定义消息转换器。用户内部服务。
 */
public abstract class AbstractInnerWebConfiguration extends AbstractWebConfiguration {

    @Autowired
    @Qualifier(value = "jsonViewHttpMessageConverter")
    private JsonViewHttpMessageConverter jsonViewHttpMessageConverter;

    /**
     * 配置消息转换规则。
     *
     * @param converters
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        //------json与对象转换器
        converters.add(jsonViewHttpMessageConverter);

        //-----字符串返回转换器
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        //解决返回乱码的问题。
        stringHttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("text/html;charset=UTF-8"));
        converters.add(stringHttpMessageConverter);

        //添加其它默认消息转换器
        super.addDefaultHttpMessageConverters(converters);
    }

    /**
     * 统一异常处理。
     * @param exceptionResolvers
     */
    @Override
    protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add(new JsonHandlerExceptionResolver(appName));
    }
}
