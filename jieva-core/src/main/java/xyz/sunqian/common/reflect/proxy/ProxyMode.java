package xyz.sunqian.common.reflect.proxy;

/**
 * This enum contains all builtin implementations of the {@link ProxyClassGenerator}.
 *
 * @author sunqian
 */
public enum ProxyMode {

    /**
     * Represents the JDK dynamic proxy implementation: {@link JdkProxyClassGenerator}.
     */
    JDK,

    /**
     * Represents the <a href="https://asm.ow2.io/">ASM</a> proxy implementation: {@link AsmProxyClassGenerator}.
     */
    ASM,
    ;
}
