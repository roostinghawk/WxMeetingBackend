package com.leadingsoft.liuw.utils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadingsoft.liuw.exception.CustomRuntimeException;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuw on 2017/6/21.
 */
public class JsonUtil {

    private static ObjectMapper mapper = new ObjectMapper();

    private JsonUtil() {
    }

    public static <T> String pojoToJson(T pojo) {
        if(pojo == null) {
            return null;
        } else {
            try {
                String e = getMapper().writeValueAsString(pojo);
                return e;
            } catch (IOException var2) {
                throw new CustomRuntimeException("Failed to convert Object2JSONString. ", new Object[]{var2});
            }
        }
    }

    public static <T> T jsonToPojo(String json, Class<T> pojoClass) {
        if(!StringUtils.hasText(json)) {
            return null;
        } else {
            try {
                return getMapper().readValue(json, pojoClass);
            } catch (IOException var3) {
                throw new CustomRuntimeException("Failed to convert JSONString2Object. ", new Object[]{var3});
            }
        }
    }

    public static <T> T jsonToPojo(Reader src, Class<T> pojoClass) throws JsonParseException, JsonMappingException, IOException {
        return getMapper().readValue(src, pojoClass);
    }

    public static <T> T jsonToPojo(String json, TypeReference<T> valueTypeRef) {
        if(!StringUtils.hasText(json)) {
            return null;
        } else {
            try {
                return getMapper().readValue(json, valueTypeRef);
            } catch (IOException var3) {
                throw new CustomRuntimeException("Failed to convert JSONString2Object. ", new Object[]{var3});
            }
        }
    }

    public static Map<String, Object> jsonToMap(String json) {
        if(!StringUtils.hasText(json)) {
            return null;
        } else {
            try {
                return (Map)getMapper().readValue(json, HashMap.class);
            } catch (IOException var2) {
                throw new CustomRuntimeException("Failed to convert JSONString2Map. ", new Object[]{var2});
            }
        }
    }

    private static ObjectMapper getMapper() {
        if(mapper != null) {
            return mapper;
        } else {
            Class var0 = JsonUtil.class;
            synchronized(JsonUtil.class) {
                if(mapper != null) {
                    return mapper;
                } else {
                    mapper = new ObjectMapper();
                    mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
                    return mapper;
                }
            }
        }
    }
}
