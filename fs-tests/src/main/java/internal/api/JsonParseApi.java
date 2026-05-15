package internal.api;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import space.sunqian.fs.data.json.JsonKit;

import java.io.InputStream;
import java.io.Reader;

public interface JsonParseApi {

    static JsonParseApi createApi(String formatType) {
        return switch (formatType) {
            case "fs" -> new FsImpl();
            case "jackson" -> new JacksonImpl();
            case "fastjson" -> new FastJsonImpl();
            default -> throw new IllegalArgumentException();
        };
    }

    Object parse(String json, Class<?> objType) throws Exception;

    Object parse(InputStream json, Class<?> objType) throws Exception;

    Object parse(Reader json, Class<?> objType) throws Exception;

    class FsImpl implements JsonParseApi {

        @Override
        public Object parse(String json, Class<?> objType) throws Exception {
            return JsonKit.parse(json).toObject(objType);
        }

        @Override
        public Object parse(InputStream json, Class<?> objType) throws Exception {
            return JsonKit.parse(json).toObject(objType);
        }

        @Override
        public Object parse(Reader json, Class<?> objType) throws Exception {
            return JsonKit.parse(json).toObject(objType);
        }
    }

    class JacksonImpl implements JsonParseApi {

        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public Object parse(String json, Class<?> objType) throws Exception {
            return mapper.readValue(json, objType);
        }

        @Override
        public Object parse(InputStream json, Class<?> objType) throws Exception {
            return mapper.readValue(json, objType);
        }

        @Override
        public Object parse(Reader json, Class<?> objType) throws Exception {
            return mapper.readValue(json, objType);
        }
    }

    class FastJsonImpl implements JsonParseApi {

        @Override
        public Object parse(String json, Class<?> objType) throws Exception {
            return JSON.parseObject(json, objType);
        }

        @Override
        public Object parse(InputStream json, Class<?> objType) throws Exception {
            return JSON.parseObject(json, objType);
        }

        @Override
        public Object parse(Reader json, Class<?> objType) throws Exception {
            return JSON.parseObject(json, objType);
        }
    }
}
