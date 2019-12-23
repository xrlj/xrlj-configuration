package com.xrlj.framework.config;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xrlj.framework.core.json.CustomObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.Arrays;

@Slf4j
public class JsonHttpMessageConverter2
		extends MappingJackson2HttpMessageConverter {

//	private static final Logger log = LoggerFactory.getLogger(JsonHttpMessageConverter2.class);

	public static final Charset UTF8 = Charset.forName("UTF-8");

	/**
	 * 用于获取JSONP调用时的回调函数名的请求参数名
	 */
	private static final String jsonpCallbackParameterName = "jsonpcallback";
	
	ObjectMapper objectMapper = new CustomObjectMapper();//默认

	public JsonHttpMessageConverter2() {
		setSupportedMediaTypes(Arrays.asList(new MediaType[]{new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8)}));
	}
	
	@Override
	protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		InputStream in = inputMessage.getBody();
		byte[] buf = new byte[1024];
		int count;
		while((count=in.read(buf))!=-1) {
			baos.write(buf, 0, count);
		}
		//解密
		String json = new String(baos.toByteArray(),UTF8.name());
		log.info(">>>>>>>>>>>>>>请求输入：" + json);
	    JavaType javaType = getJavaType(clazz, null);
	    //转换
	    return this.objectMapper.readValue(json, javaType);
	}
	
	@Override
	protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
				.getRequestAttributes()).getRequest();

		//使用Jackson的ObjectMapper将Java对象转换成Json String
	    ObjectMapper mapper = this.objectMapper;
	    String json = mapper.writeValueAsString(object);
		log.info(">>>>>>>>>>>>>>请求输出：" + json);
	    //加密
//	    String result = AESUtils.jdkAESEncode(keyStr, json);
		StringBuilder sb = new StringBuilder();
		String callbackName = request.getParameter(jsonpCallbackParameterName);
		if (callbackName != null) {
			sb.append(callbackName);
			sb.append("(");
			sb.append(json);
			sb.append(")");
		} else {
			sb.append(json);
		}

	    //输出
	    outputMessage.getBody().write((sb.toString()).getBytes());
		
//		super.writeInternal(object, type, outputMessage);

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
