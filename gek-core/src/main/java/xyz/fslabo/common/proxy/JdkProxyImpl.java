package xyz.fslabo.common.proxy;

import xyz.fslabo.common.base.Jie;
import xyz.fslabo.common.coll.JieColl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Predicate;

final class JdkProxyImpl<T> implements GekProxy<T> {

    private final Class<?>[] superInterfaces;
    private final Map<MethodSignature, GekProxyMethod> methodMap;

    JdkProxyImpl(Iterable<Class<?>> superInterfaces, Map<Predicate<Method>, GekProxyMethod> proxyMap) {
        if (JieColl.isEmpty(superInterfaces)) {
            throw new GekProxyException("No super interface to be proxied.");
        }
        this.superInterfaces = JieColl.toArray(superInterfaces, Class.class);
        if (JieColl.isEmpty(proxyMap)) {
            this.methodMap = Collections.emptyMap();
            return;
        }
        this.methodMap = new HashMap<>();
        for (Class<?> superInterface : superInterfaces) {
            Method[] methods = superInterface.getMethods();
            proxyMap.forEach((predicate, proxy) -> {
                for (Method method : methods) {
                    if (predicate.test(method)) {
                        methodMap.put(new MethodSignature(method), proxy);
                    }
                }
            });
        }
    }

    @Override
    public T newInstance() {
        Object inst;
        if (methodMap.isEmpty()) {
            inst = Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                superInterfaces,
                (proxy, method, args) -> method.invoke(proxy, args));
        } else {
            inst = Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                superInterfaces,
                (proxy, method, args) -> {
                    GekProxyMethod proxyMethod = methodMap.get(new MethodSignature(method));
                    if (proxyMethod == null) {
                        return method.invoke(proxy, args);
                    }
                    return proxyMethod.invokeProxy(args, method, objs -> {
                        try {
                            return method.invoke(proxy, objs);
                        } catch (InvocationTargetException e) {
                            throw e.getCause();
                        }
                    });
                });
        }
        return Jie.as(inst);
    }

    private static final class MethodSignature {

        private final String name;
        private final List<Class<?>> paramTypes;

        private int hash = 0;

        private MethodSignature(Method method) {
            this.name = method.getName();
            this.paramTypes = Arrays.asList(method.getParameterTypes());
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodSignature that = (MethodSignature) o;
            return Objects.equals(name, that.name) && Objects.equals(paramTypes, that.paramTypes);
        }

        @Override
        public int hashCode() {
            if (hash == 0) {
                hash = Objects.hash(name, paramTypes);
                if (hash == 0) {
                    hash = 1;
                }
            }
            return hash;
        }
    }
}
