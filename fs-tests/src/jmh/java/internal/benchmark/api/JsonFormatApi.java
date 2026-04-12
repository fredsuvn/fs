package internal.benchmark.api;

import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import space.sunqian.fs.data.json.JsonKit;

public interface JsonFormatApi {

    static JsonFormatApi createFormat(String formatType) {
        return switch (formatType) {
            case "fs" -> new FsImpl();
            case "jackson" -> new JacksonImpl();
            case "fastjson" -> new FastJsonImpl();
            default -> throw new IllegalArgumentException();
        };
    }

    String toJsonString(Object obj) throws Exception;

    class FsImpl implements JsonFormatApi {

        @Override
        public String toJsonString(Object obj) throws Exception {
            return JsonKit.toJsonString(obj);
        }
    }

    class JacksonImpl implements JsonFormatApi {

        private final ObjectMapper mapper = new ObjectMapper();

        @Override
        public String toJsonString(Object obj) throws Exception {
            return mapper.writeValueAsString(obj);
        }
    }

    class FastJsonImpl implements JsonFormatApi {

        @Override
        public String toJsonString(Object obj) throws Exception {
            return JSON.toJSONString(obj);
        }
    }
}
