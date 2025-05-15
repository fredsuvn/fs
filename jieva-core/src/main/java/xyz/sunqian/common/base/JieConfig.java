package xyz.sunqian.common.base;

import xyz.sunqian.common.base.exception.JieRuntimeException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Configurations of Jieva.
 *
 * @author fredsuvn
 */
public class JieConfig {

    private static final Map<String, String> CONFIGS = Collections.unmodifiableMap(getAll());

    private static Map<String, String> getAll() {
        try {
            InputStream in = JieConfig.class.getResourceAsStream("/jie.properties");
            Properties props = new Properties();
            props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
            Map<String, String> result = new LinkedHashMap<>();
            props.forEach((k, v) -> result.put(String.valueOf(k), v == null ? null : v.toString()));
            in.close();
            return result;
        } catch (IOException e) {
            throw new JieRuntimeException(e);
        }
    }

    /**
     * Returns version of jie.
     *
     * @return version of jie
     */
    public static String version() {
        return CONFIGS.get("version");
    }
}
