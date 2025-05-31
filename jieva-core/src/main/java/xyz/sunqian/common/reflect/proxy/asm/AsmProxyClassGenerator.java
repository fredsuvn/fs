package xyz.sunqian.common.reflect.proxy.asm;

import xyz.sunqian.common.reflect.proxy.ProxyClass;
import xyz.sunqian.common.reflect.proxy.ProxyClassGenerator;
import xyz.sunqian.common.reflect.proxy.ProxyException;
import xyz.sunqian.common.reflect.proxy.ProxyMethodHandler;

/**
 * The <a href="https://asm.ow2.io/">ASM</a> implementation for {@link ProxyClassGenerator}.
 *
 * @author sunqian
 */
public class AsmProxyClassGenerator implements ProxyClassGenerator {

    @Override
    public ProxyClass generate(Class<?>[] proxied, ProxyMethodHandler methodHandler) throws ProxyException {
        return null;
    }
}
