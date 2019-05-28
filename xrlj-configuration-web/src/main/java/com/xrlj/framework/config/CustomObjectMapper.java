package com.xrlj.framework.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomObjectMapper extends ObjectMapper {

    public CustomObjectMapper() {
        super();

        //对象处理
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeString("");//把null转为空,对象转为空字符串
            }
        });


        SimpleModule module = new SimpleModule();

        //把boolean转成1或0表示
       /* module.addSerializer(boolean.class, new JsonSerializer<Boolean>() {
            @Override
            public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeNumber(value ? 1 : 0);
            }
        });*/

        //把Boolean转成1或0表示
        /*module.addSerializer(Boolean.class, new JsonSerializer<Boolean>() {
            @Override
            public void serialize(Boolean value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
                jgen.writeNumber(value ? 1 : 0);
            }
        });*/

        //日期
        module.addSerializer(Date.class, new JsonSerializer<Date>() {
            @Override
            public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                gen.writeString(sdf.format(value));
            }
        });

        this.registerModule(module);

    }

}
