package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.OutParam;
import xyz.sunqian.common.base.system.JvmKit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utilities kit for dynamic proxy.
 *
 * @author sunqian
 */
public class ProxyKit {

    /**
     * Returns a map that contains all proxiable methods of the given class and interfaces. Its keys are the specified
     * proxied types, and the values are the proxiable methods under that type. A proxiable method is public or
     * protected, and non-static and non-final. If there are methods with the same name and same JVM descriptor, only
     * the first one encountered is added into the map.
     * <p>
     * This method is a helpful method for the {@link ProxyMaker} implementations.
     *
     * @param proxiedClass the class to be proxied, may be {@code null} if it is {@link Object}
     * @param interfaces   the interfaces to be proxied
     * @param proxyHandler the proxy handler
     * @return a map that contains all proxiable methods of the given class and interfaces
     */
    public static @Nonnull Map<Class<?>, List<Method>> getProxiableMethods(
        @Nullable Class<?> proxiedClass,
        @Nonnull List<@Nonnull Class<?>> interfaces,
        @Nonnull ProxyHandler proxyHandler
    ) {
        Map<Class<?>, List<Method>> result = new LinkedHashMap<>();
        Set<String> ids = new HashSet<>();
        Class<?> superclass = proxiedClass == null ? Object.class : proxiedClass;
        for (Method method : superclass.getMethods()) {
            putProxiableMethod(method, proxyHandler, ids, superclass, result);
        }
        for (Method method : superclass.getDeclaredMethods()) {
            putProxiableMethod(method, proxyHandler, ids, superclass, result);
        }
        for (Class<?> anInterface : interfaces) {
            for (Method method : anInterface.getMethods()) {
                putProxiableMethod(method, proxyHandler, ids, anInterface, result);
            }
        }
        return result;
    }

    private static boolean isProxiable(@Nonnull Method method, @Nonnull ProxyHandler handler) {
        if (method.isSynthetic()) {
            return false;
        }
        int mod = method.getModifiers();
        if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
            return false;
        }
        if (Modifier.isPublic(mod) || Modifier.isProtected(mod)) {
            return handler.shouldProxyMethod(method);
        }
        return false;
    }

    private static void putProxiableMethod(
        @Nonnull Method method,
        @Nonnull ProxyHandler handler,
        @Nonnull Set<String> ids,
        @Nonnull Class<?> type,
        @Nonnull @OutParam Map<Class<?>, List<Method>> result
    ) {
        if (isProxiable(method, handler)) {
            String methodId = buildMethodId(method);
            if (!ids.contains(methodId)) {
                ids.add(methodId);
                result.compute(type, (k, v) -> {
                    if (v == null) {
                        v = new ArrayList<>();
                    }
                    v.add(method);
                    return v;
                });
            }
        }
    }

    private static @Nonnull String buildMethodId(@Nonnull Method method) {
        return method.getName() + ":" + JvmKit.getDescriptor(method);
    }
}
