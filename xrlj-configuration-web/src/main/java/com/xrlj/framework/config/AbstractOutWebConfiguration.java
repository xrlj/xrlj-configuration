package com.xrlj.framework.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.HttpMessageConverter;

import java.util.List;

/**
 * 定义消息转换器。对外服务，一般是网关。
 */
public abstract class AbstractOutWebConfiguration extends AbstractWebConfiguration {

    @Autowired
    @Qualifier(value = "jsonViewHttpMessageConverterOpen")
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
    }

}
