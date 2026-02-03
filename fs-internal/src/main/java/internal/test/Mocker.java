package internal.test;

import org.mockito.Mockito;
import space.sunqian.annotation.Nonnull;

import java.lang.reflect.Array;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.IntFunction;

/**
 * Mocker to mock classes.
 *
 * @author sunqian
 */
public class Mocker {

    /**
     * Mocks a class, including private types.
     *
     * @param clazz the class to mock
     * @return the mocked class
     */
    @SuppressWarnings("unchecked")
    public static <T> T mock(@Nonnull Class<T> clazz) {
        return (T) mock0(clazz);
    }

    private static Object mock0(@Nonnull Class<?> clazz) {
        if (clazz.isPrimitive() || Number.class.isAssignableFrom(clazz)) {
            if (Objects.equals(clazz, int.class) || Objects.equals(clazz, Integer.class)) {
                return 0;
            }
            if (Objects.equals(clazz, long.class) || Objects.equals(clazz, Long.class)) {
                return 0L;
            }
            if (Objects.equals(clazz, float.class) || Objects.equals(clazz, Float.class)) {
                return 0.0f;
            }
            if (Objects.equals(clazz, double.class) || Objects.equals(clazz, Double.class)) {
                return 0.0;
            }
            if (Objects.equals(clazz, char.class)) {
                return (char) 0;
            }
            if (Objects.equals(clazz, boolean.class)) {
                return false;
            }
            if (Objects.equals(clazz, byte.class) || Objects.equals(clazz, Byte.class)) {
                return (byte) 0;
            }
            if (Objects.equals(clazz, short.class) || Objects.equals(clazz, Short.class)) {
                return (short) 0;
            }
            if (Objects.equals(clazz, BigDecimal.class)) {
                return BigDecimal.ZERO;
            }
            if (Objects.equals(clazz, BigInteger.class)) {
                return BigInteger.ZERO;
            }
            return null;
        }
        if (Objects.equals(clazz, String.class)) {
            return "";
        }
        if (Objects.equals(clazz, Character.class)) {
            return (char) 0;
        }
        if (Objects.equals(clazz, Boolean.class)) {
            return false;
        }
        if (clazz.isArray()) {
            return Array.newInstance(clazz.getComponentType(), 0);
        }
        if (clazz.isEnum()) {
            Object[] constants = clazz.getEnumConstants();
            return constants.length > 0 ? constants[0] : null;
        }
        if (Objects.equals(clazz, Collection.class)) {
            return Collections.emptyList();
        }
        if (Objects.equals(clazz, IntFunction.class)) {
            return (IntFunction<Object>) Object[]::new;
        }
        if (Modifier.isFinal(clazz.getModifiers())) {
            return null;
        }
        return Mockito.mock(clazz);
    }

    private Mocker() {
    }
}
