package xyz.sunqian.common.reflect.proxy;

import java.lang.reflect.Method;
import java.util.List;

/**
 * This interface is used to build proxy class ({@link ProxyClass}).
 * <p>
 * A proxy class is a subtype extends a class ({@link #superClass(Class)}) and/or implements a list of interfaces
 * ({@link #interfaces(List)}), in order of clause of the declaration. For example, let the proxy class
 * {@code SomeProxy} declare as:
 * <pre>
 *     public class SomeProxy extends SomeSuper implements SomeInter
 * </pre>
 * The {@code SomeSuper} is the super class and {@code SomeInter} is the interface. At least one of the superclass or
 * interfaces must be set.
 * <p>
 * A proxy class proxies methods by overriding. The method proxy handler ({@link #proxyHandler(MethodProxyHandler)})
 * tells which methods need to be overridden and proxied, and how.
 * <p>
 * There are two default implementations: {@link AsmProxyBuilder} and {@link JdkDynamicProxyBuilder}.
 */
public interface ProxyBuilder {

    /**
     * Sets super class extended by proxy class.
     *
     * @param superClass super class extended by proxy class
     * @return this builder
     */
    ProxyBuilder superClass(Class<?> superClass);

    /**
     * Sets interfaces implemented by proxy class.
     *
     * @param interfaces interfaces implemented by proxy class
     * @return this builder
     */
    ProxyBuilder interfaces(List<Class<?>> interfaces);

    /**
     * Sets method proxy handler for proxy class.
     *
     * @param handler method proxy handler for proxy class
     * @return this builder
     */
    ProxyBuilder proxyHandler(MethodProxyHandler handler);

    /**
     * Sets whether proxy class is final.
     *
     * @param isFinal whether proxy class is final
     * @return this builder
     */
    ProxyBuilder isFinal(boolean isFinal);

    /**
     * Sets class name ({@link Class#getName()}) of proxy class. This is not required. If not set, the generated class
     * will have a default name.
     *
     * @param className class name ({@link Class#getName()})
     * @return this builder
     */
    ProxyBuilder className(String className);

    /**
     * Sets class loader for proxy class. This is not required.
     *
     * @param classLoader class loader for proxy class
     * @return this builder
     */
    ProxyBuilder classLoader(ClassLoader classLoader);

    /**
     * Generates and returns a new proxy class.
     * <p>
     * Typically, the builder creates a subclass that inherits from {@link #superClass(Class)} and/or
     * {@link #interfaces(List)} (if been set). Then tests each method obtained from the {@link Class#getMethods()} by
     * {@link MethodProxyHandler#needsProxy(Method)} (from {@link #proxyHandler(MethodProxyHandler)}). If the handler
     * returns {@code true}, the method will be proxied via overriding.
     *
     * @return a new proxy class
     * @throws ProxyException if any problem occurs
     */
    ProxyClass build() throws ProxyException;
}
