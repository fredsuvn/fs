package internal.api;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import space.sunqian.fs.data.json.JsonKit;

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

    class FsImpl implements JsonParseApi {

        @Override
        public Object parse(String json, Class<?> objType) throws Exception {
            return JsonKit.parse(json).toObject(objType);
        }
    }

    class JacksonImpl implements JsonParseApi {

        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public Object parse(String json, Class<?> objType) throws Exception {
            return mapper.readValue(json, objType);
        }
    }

    class FastJsonImpl implements JsonParseApi {

        @Override
        public Object parse(String json, Class<?> objType) throws Exception {
            return JSON.parseObject(json, objType);
        }
    }
}
