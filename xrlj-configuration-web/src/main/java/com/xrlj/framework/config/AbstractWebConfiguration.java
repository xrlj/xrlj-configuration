package com.xrlj.framework.config;

import com.xrlj.framework.spring.mvc.api.withhttpheader.CustomRequestMappingHandlerMapping;
import com.xrlj.framework.spring.mvc.sensitive.SensitiveFormatAnnotationFormatterFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 定义消息转换器
 */
public abstract class AbstractWebConfiguration extends WebMvcConfigurationSupport {

    @Value("${spring.application.name}")
    protected String appName;

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
