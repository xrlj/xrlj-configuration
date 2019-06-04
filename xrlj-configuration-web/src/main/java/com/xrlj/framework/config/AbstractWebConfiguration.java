package com.xrlj.framework.config;

import com.xrlj.framework.spring.mvc.api.withhttpheader.CustomRequestMappingHandlerMapping;
import com.xrlj.framework.spring.mvc.sensitive.SensitiveFormatAnnotationFormatterFactory;
import org.hibernate.validator.HibernateValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.Validator;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.validation.Validation;
import javax.validation.ValidatorFactory;
import java.util.List;

/**
 * 定义消息转换器
 */
public abstract class AbstractWebConfiguration extends WebMvcConfigurationSupport {

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
        converters.add(new JsonHttpMessageConverter2());

        //-----字符串返回转换器
        /*StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        //解决返回乱码的问题。
        stringHttpMessageConverter.setSupportedMediaTypes(MediaType.parseMediaTypes("text/html;charset=UTF-8"));
        converters.add(stringHttpMessageConverter);*/

        //添加其它默认消息转换器
//        super.addDefaultHttpMessageConverters(converters);
    }

    @Override
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new CustomRequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        return handlerMapping;
    }


    @Override
    protected Validator getValidator() {
//        ValidatorFactory validatorFactory = Validation.byProvider(HibernateValidator.class).configure().failFast(true).buildValidatorFactory();
//        javax.validation.Validator validator = validatorFactory.getValidator();
        return super.getValidator();
    }

    @Override
    protected void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldAnnotation(new SensitiveFormatAnnotationFormatterFactory(s -> {
            if ("色情".equals(s)) {
                return "参数中包含敏感词";
            }
            return s;
        }));
        super.addFormatters(registry);
    }

}
