package xyz.sunqian.common.reflect.proxy;

/**
 * This enum indicates the way of the proxy implementation.
 *
 * @author sunqian
 */
public enum ProxyMode {

    /**
     * The JDK dynamic proxy.
     */
    JDK,

    /**
     * The <a href="https://asm.ow2.io/">ASM</a> proxy.
     */
    ASM,
    ;
}
