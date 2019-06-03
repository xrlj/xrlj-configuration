package com.xrlj.framework.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xrlj.framework.dao.RedisDao;
import com.xrlj.framework.filter.IndexFilter;
import com.xrlj.framework.spring.mvc.api.ApiResult;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.hateoas.RelProvider;
import org.springframework.hateoas.hal.CurieProvider;
import org.springframework.hateoas.hal.Jackson2HalModule;
import org.springframework.http.MediaType;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.MediaTypes.HAL_JSON;

/**
 * 功能简述:<br>
 *
 * @author zmt
 * @create 2018-03-08 下午5:28
 * @updateTime
 * @since 1.0.0
 */
@Configuration
public class WebMainConfig {

    @Autowired
    private ListableBeanFactory beanFactory;

    private static CurieProvider getCurieProvider(BeanFactory factory) {

        try {
            return factory.getBean(CurieProvider.class);
        } catch (NoSuchBeanDefinitionException e) {
            return null;
        }
    }

    private static final String DELEGATING_REL_PROVIDER_BEAN_NAME = "_relProvider";
//    private static final String LINK_DISCOVERER_REGISTRY_BEAN_NAME = "_linkDiscovererRegistry";
//    private static final String HAL_OBJECT_MAPPER_BEAN_NAME = "_halObjectMapper";

    /**
     * 消息转换。集成超媒体连接。拥有内部服务
     * @return
     */
    @Bean
    @Primary
    @Qualifier(value = "jsonViewHttpMessageConverter")
    public JsonViewHttpMessageConverter jsonViewHttpMessageConverter() {

        return setJsonViewHttpMessageConverter(Object.class);
    }

    /**
     * 对外服务消息转换。必须是继承自VBase的类型。
     * @return
     */
    @Bean
    @Qualifier(value = "jsonViewHttpMessageConverterOpen")
    public JsonViewHttpMessageConverter jsonViewHttpMessageConverterOpen() {

        return setJsonViewHttpMessageConverter(ApiResult.class);
    }

    private JsonViewHttpMessageConverter setJsonViewHttpMessageConverter(Class clazz) {
        //Need to override some behaviour in the HAL Serializer...so let's make our own
        CurieProvider curieProvider = getCurieProvider(beanFactory);
        RelProvider relProvider = beanFactory.getBean(DELEGATING_REL_PROVIDER_BEAN_NAME, RelProvider.class);
//        ObjectMapper halObjectMapper = beanFactory.getBean(HAL_OBJECT_MAPPER_BEAN_NAME, ObjectMapper.class);
        ObjectMapper halObjectMapper = new CustomObjectMapper();
        halObjectMapper.registerModule(new Jackson2HalModule());
        halObjectMapper.setHandlerInstantiator(new Jackson2HalModule.HalHandlerInstantiator(relProvider, curieProvider,null));

        JsonViewHttpMessageConverter halConverter = new JsonViewHttpMessageConverter(clazz);
//        JsonViewHttpMessageConverter halConverter = new JsonViewHttpMessageConverter(VBase.class);//请求Bean,响应Bean必须继承VBase
        List<MediaType> list = new ArrayList<>();
        list.add(MediaType.APPLICATION_JSON_UTF8);
        list.add(HAL_JSON);
        halConverter.setSupportedMediaTypes(list);
        halConverter.setObjectMapper(halObjectMapper);

        return halConverter;
    }

    /**
     * 设置跨域请求。过滤器。
     * @param
     * @return
     */
    @Bean
    public CorsFilter corsFilter(CustomCorsProperties customCorsProperties) {
        //1.添加CORS配置信息
        CorsConfiguration config = new CorsConfiguration();
        //放行哪些原始域
        config.addAllowedOrigin(customCorsProperties.getAllowedOrigins());
        //是否发送Cookie信息
        config.setAllowCredentials(customCorsProperties.getAllowCredentials());
        //放行哪些原始域(请求方式)
        config.addAllowedMethod(customCorsProperties.getAllowedMethods());
        //放行哪些原始域(头部信息)
        config.addAllowedHeader(customCorsProperties.getAllowedHeaders());
        //暴露哪些头部信息（因为跨域访问默认不能获取全部头部信息）
//        config.addExposedHeader("header-1,header-2");

        //2.添加映射路径
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", config);

        //3.返回新的CorsFilter.
        return new CorsFilter(configSource);
    }

    @Bean
    public FilterRegistrationBean indexFilterRegistration() {
        FilterRegistrationBean registration = new FilterRegistrationBean(new IndexFilter(), new ServletRegistrationBean[0]);
        registration.addUrlPatterns(new String[]{"/*"});
        registration.setName("indexFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 初始化tomcat服务器工厂类。注入该类可以获取tomcat服务器的一些信息，如启动的端口等。
     * @return 返回工厂对象
     */
    @Bean
    public TomcatServletWebServerFactory tomcatServletWebServerFactory() {
        return new TomcatServletWebServerFactory();

    }

    @Bean
    public RedisDao redisDao() {
        return new RedisDao();
    }
}
