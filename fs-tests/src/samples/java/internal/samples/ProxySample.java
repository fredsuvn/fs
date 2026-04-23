package internal.samples;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.dynamic.proxy.ProxyHandler;
import space.sunqian.fs.dynamic.proxy.ProxyInvoker;
import space.sunqian.fs.dynamic.proxy.ProxyMaker;

import java.lang.reflect.Method;
import java.util.Collections;

/**
 * Sample: Dynamic Proxy Usage
 * <p>
 * Purpose: Demonstrate how to use the dynamic proxy utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Create dynamic proxies for classes
 *   </li>
 *   <li>
 *     Intercept method calls with custom logic
 *   </li>
 *   <li>
 *     Customize proxy behavior
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link ProxyMaker}: Creates dynamic proxies using ASM
 *   </li>
 *   <li>
 *     {@link ProxyHandler}: Handles proxy invocations
 *   </li>
 *   <li>
 *     {@link ProxyInvoker}: Invokes the original method
 *   </li>
 * </ul>
 */
public class ProxySample {

    public static void main(String[] args) {
        demonstrateDynamicProxy();
    }

    /**
     * Demonstrates creating and using a dynamic proxy.
     */
    public static void demonstrateDynamicProxy() {
        System.out.println("=== Dynamic Proxy Demonstration ===");

        // Create dynamic proxy for Hello class
        Hello proxy = ProxyMaker.byAsm().make(Hello.class, Collections.emptyList(), new ProxyHandler() {

            @Override
            public boolean needsProxy(@Nonnull Method method) {
                return method.getName().equals("hello");
            }

            @Override
            public @Nonnull Object invoke(
                @Nonnull Object proxy, @Nonnull Method method, @Nonnull ProxyInvoker invoker, Object @Nonnull ... args
            ) throws Throwable {
                System.out.println("Proxy: Before invoking hello()");
                Object result = invoker.invokeSuper(proxy, args);
                System.out.println("Proxy: After invoking hello()");
                return result + "[proxy]";
            }
        }).newInstance();

        // Call method on proxy
        String proxyResult = proxy.hello();
        System.out.println("Proxy result: " + proxyResult);
    }

    /**
     * Sample class for proxy demonstration.
     */
    public static class Hello {

        public String hello() {
            System.out.println("Hello.hello() called");
            return "hello";
        }
    }
}