package xyz.sunqian.common.reflect.proxy;

import xyz.sunqian.annotations.Nonnull;

/**
 * Utilities kit for dynamic proxy.
 *
 * @author sunqian
 */
public class ProxyKit {

    /**
     * Returns a new proxy class generator for the specified mode.
     *
     * @param mode the specified mode to specifies the actual underlying implementation of the generation
     * @return a new proxy class generator for the specified mode
     */
    public static @Nonnull ProxyClassGenerator newGenerator(@Nonnull ProxyMode mode) {
        if (mode == ProxyMode.ASM) {
            return new AsmProxyClassGenerator();
        }
        return new JdkProxyClassGenerator();
    }
}
