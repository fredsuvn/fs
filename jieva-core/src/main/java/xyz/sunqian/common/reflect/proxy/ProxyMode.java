package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.util.List;

/**
 * This enum indicates the way of the proxy implementation. It is itself a delegate implementation of
 * {@link ProxyClassGenerator}, holding corresponding actual implementations.
 *
 * @author sunqian
 */
public enum ProxyMode implements ProxyClassGenerator {

    /**
     * The JDK dynamic proxy via {@link JdkProxyClassGenerator}.
     */
    JDK(new JdkProxyClassGenerator()),

    /**
     * The <a href="https://asm.ow2.io/">ASM</a> proxy via {@link AsmProxyClassGenerator}.
     */
    ASM(new AsmProxyClassGenerator()),
    ;

    private final ProxyClassGenerator generator;

    ProxyMode(ProxyClassGenerator generator) {
        this.generator = generator;
    }

    @Override
    public @Nonnull ProxyClass generate(
        @Nullable Class<?> proxiedClass, @Nonnull List<Class<?>> interfaces, @Nonnull ProxyMethodHandler methodHandler
    ) throws ProxyException {
        return generator.generate(proxiedClass, interfaces, methodHandler);
    }
}
