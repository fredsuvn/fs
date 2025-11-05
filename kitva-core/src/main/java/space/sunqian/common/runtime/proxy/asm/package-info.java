/**
 * Provides proxy maker implementations by ASM, using code similar to the following:
 * <pre>{@code
 * ProxySpec spec = ProxyMaker.byAsm().make(cls, interfaces, handler);
 * ProxyClass ac = spec.newInstance();
 * }</pre>
 * Core implementations:
 * <ul>
 *     <li>{@link space.sunqian.common.runtime.proxy.asm.AsmProxyMaker}</li>
 * </ul>
 */
package space.sunqian.common.runtime.proxy.asm;