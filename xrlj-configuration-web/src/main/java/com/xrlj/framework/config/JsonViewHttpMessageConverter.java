package com.xrlj.framework.config;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.mvc.TypeConstrainedMappingJackson2HttpMessageConverter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * json和bean之间转换。
 */
@Slf4j
public class JsonViewHttpMessageConverter
		extends TypeConstrainedMappingJackson2HttpMessageConverter {

	ObjectMapper objectMapper = new CustomObjectMapper();//默认

	/**
	 * Creates a new {@link TypeConstrainedMappingJackson2HttpMessageConverter} for the given type.
	 *
	 * @param type must not be {@literal null}.消息转换必须继承的类型。只有继承该类型的传参或者返回才会进入该转化器。
	 */
	public JsonViewHttpMessageConverter(Class<?> type) {
		super(type);
		getObjectMapper().setSerializerFactory(getObjectMapper().getSerializerFactory().withSerializerModifier(new MyBeanSerializerModifier()));
	}

    @Override
    public Object read(Type type, Class<?> contextClass, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
		// 解密
//		String json = AESUtil.decrypt (inputMessage.getBody ());
        JavaType javaType = getJavaType(type, contextClass);
        //转换
		Object reqData = this.objectMapper.readValue(inputMessage.getBody(), javaType);

		log.info(">>>>>>>>>>>>>>请求输入" + JSON.toJSONString(reqData));

        return reqData;
//		return super.read(type,contextClass,inputMessage);
    }

    @Override
	protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		//使用Jackson的ObjectMapper将Java对象转换成Json String
	    ObjectMapper mapper = this.objectMapper;
	    String json = mapper.writeValueAsString(object);
		log.info(">>>>>>>>>>>>>>>请求输出" + json);
	    //加密
//	    String result = AESUtils.jdkAESEncode(keyStr, json);
	    //输出
	    outputMessage.getBody().write(json.getBytes());
	}
	
	@Override
	public ObjectMapper getObjectMapper() {
		return this.objectMapper;
	}
	
	@Override
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
