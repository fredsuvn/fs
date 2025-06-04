package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;

import java.util.function.Supplier;

/**
 * This enum contains all builtin implementations of the {@link ProxyClassGenerator}.
 *
 * @author sunqian
 */
public enum ProxyMode {

    /**
     * Represents the JDK dynamic proxy implementation: {@link JdkProxyClassGenerator}.
     */
    JDK(JdkProxyClassGenerator::new),

    /**
     * Represents the <a href="https://asm.ow2.io/">ASM</a> proxy implementation: {@link AsmProxyClassGenerator}.
     */
    ASM(AsmProxyClassGenerator::new),
    ;

    private final Supplier<ProxyClassGenerator> supplier;

    ProxyMode(Supplier<ProxyClassGenerator> supplier) {
        this.supplier = supplier;
    }

    /**
     * Returns a new generator represented by this mode.
     *
     * @return a new generator represented by this mode
     */
    public @Nonnull ProxyClassGenerator newGenerator() {
        return supplier.get();
    }
}
