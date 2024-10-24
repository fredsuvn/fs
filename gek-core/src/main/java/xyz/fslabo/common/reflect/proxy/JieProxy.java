package xyz.fslabo.common.reflect.proxy;

import java.lang.reflect.Proxy;

/**
 * This is a static utilities class provides utilities for {@code class proxy}.
 *
 * @author fredsuvn
 */
public class JieProxy {

    /**
     * Returns a new proxy builder implemented by JDK dynamic proxy ({@link Proxy}).
     *
     * @return a new proxy builder implemented by JDK dynamic proxy ({@link Proxy})
     * @see JdkDynamicProxyBuilder
     */
    public static ProxyBuilder jdkProxyBuilder() {
        return new JdkDynamicProxyBuilder();
    }

    /**
     * Returns a new proxy builder implemented by <a href="https://asm.ow2.io/">ASM</a>. The runtime environment must
     * have asm package {@code org.objectweb.asm}.
     *
     * @return a new proxy builder implemented by <a href="https://asm.ow2.io/">ASM</a>
     * @see AsmProxyBuilder
     */
    public static ProxyBuilder asmProxyBuilder() {
        return new AsmProxyBuilder();
    }
}
