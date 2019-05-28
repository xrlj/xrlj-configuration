package com.xrlj.framework.config;//package com.xrlj.framework.spring.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.format.FormatterRegistry;
//import org.springframework.http.converter.HttpMessageConverter;
//import org.springframework.validation.Validator;
//import org.springframework.web.servlet.config.annotation.EnableWebMvc;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//import java.util.List;
//
///**
// * 定义消息转换器
// */
////@Configuration
////@EnableWebMvc
//public class WebConfiguration extends WebMvcConfigurerAdapter {
//
//    //定义自己的消息转换机制
//    @Override
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        super.configureMessageConverters(converters);
//        /*Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder()
//                .indentOutput(true)
//                .dateFormat(new SimpleDateFormat("yyyy-MM-dd"))
//                .modulesToInstall(new ParameterNamesModule());
//        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        converters.add(new MappingJackson2XmlHttpMessageConverter(builder.xml().build()));*/
//    }
//
//    //转换与格式化
//    @Override
//    public void addFormatters(FormatterRegistry registry) {
//        super.addFormatters(registry);
//    }
//
//    /**
//     * 定义全局验证器
//     *
//     * 同时添加局部验证器
//     * @Controller public class MyController {
//     * @InitBinder protected void initBinder(WebDataBinder binder) {
//     * binder.addValidators(new FooValidator());
//     * }
//     * }
//     */
//    @Override
//    public Validator getValidator() {
//        return super.getValidator();
////         return new GlobalValidator();
//    }
//}
