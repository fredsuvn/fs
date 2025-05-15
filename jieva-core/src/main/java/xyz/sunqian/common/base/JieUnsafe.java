package xyz.sunqian.common.base;

import xyz.sunqian.common.base.exception.UnsafeException;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;

/**
 * Unsafe for Jieva.
 *
 * @author sunq62
 */
public class JieUnsafe {

    /**
     * Returns a {@link MethodHandles.Lookup} for specified class.
     *
     * @param cls specified class
     * @return a {@link MethodHandles.Lookup} for specified class
     * @throws UnsafeException for any problem
     */
    public static MethodHandles.Lookup lookup(Class<?> cls) throws UnsafeException {
        try {
            if (JieSystem.isJava8Higher()) {
                return MethodHandles.lookup();
            }
            Constructor<MethodHandles.Lookup> constructor = MethodHandles.Lookup.class
                .getDeclaredConstructor(Class.class);
            constructor.setAccessible(true);
            return constructor.newInstance(cls).in(cls);
        } catch (Exception e) {
            throw new UnsafeException(e);
        }
    }
}
