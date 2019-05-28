package com.xrlj.framework.config;

/**
 * Created by lujijiang on 2016/12/3.
 */

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * JSON消息转换器，使用Fastjson作为转换器
 * */
public class JsonHttpMessageConverter extends AbstractHttpMessageConverter<Object> {

    public static final Charset UTF8 = Charset.forName("UTF-8");

    public JsonHttpMessageConverter() {
        super(new MediaType[]{new MediaType("application", "json", UTF8), new MediaType("application", "*+json", UTF8)});
    }

    /**
     * 用于获取JSONP调用时的回调函数名的请求参数名
     */
    private String jsonpCallbackParameterName = "jsonpcallback";

    private Charset charset = Charset.forName("UTF-8");

    public String getJsonpCallbackParameterName() {
        return jsonpCallbackParameterName;
    }

    public void setJsonpCallbackParameterName(String jsonpCallbackParameterName) {
        this.jsonpCallbackParameterName = jsonpCallbackParameterName;
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    protected Object readInternal(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream in = inputMessage.getBody();
        byte[] buf = new byte[1024];
        int count;
        while((count=in.read(buf))!=-1) {
            baos.write(buf, 0, count);
        }
        String json = new String(baos.toByteArray(),charset.name());
        Object object = JSON.parseObject(json, clazz, new Feature[0]);
        return object;
    }

    @Override
    protected void writeInternal(Object obj, HttpOutputMessage outputMessage)
            throws IOException, HttpMessageNotWritableException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getRequest();
        OutputStream os = outputMessage.getBody();
        StringBuilder out = new StringBuilder();
        String json = JSON.toJSONString(obj);
        String callbackName = request.getParameter(jsonpCallbackParameterName);
        if (callbackName != null) {
            out.append(callbackName);
            out.append("(");
            out.append(json);
            out.append(")");
        } else {
            out.append(json);
        }
        String text = out.toString();
        byte[] bytes = text.getBytes(charset);
        os.write(bytes);
    }

}
