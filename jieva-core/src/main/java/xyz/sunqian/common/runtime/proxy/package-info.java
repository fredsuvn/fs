/**
 * Provides makers to rapidly create proxy class with minimal effort, using code similar to the following:
 * <pre>{@code
 * ProxySpec spec = ProxyMaker.byAsm().make(cls, interfaces, handler);
 * ProxyClass ac = spec.newInstance();
 * }</pre>
 * <p>
 * Utilities:
 * <ul>
 *     <li>{@link xyz.sunqian.common.runtime.proxy.ProxyKit}</li>
 * </ul>
 * Core interfaces:
 * <ul>
 *     <li>{@link xyz.sunqian.common.runtime.proxy.ProxyMaker}</li>
 *     <li>{@link xyz.sunqian.common.runtime.proxy.ProxySpec}</li>
 *     <li>{@link xyz.sunqian.common.runtime.proxy.ProxyHandler}</li>
 *     <li>{@link xyz.sunqian.common.runtime.proxy.ProxyInvoker}</li>
 *     <li>{@link xyz.sunqian.common.runtime.proxy.AsmProxyMaker}</li>
 *     <li>{@link xyz.sunqian.common.runtime.proxy.JdkProxyMaker}</li>
 * </ul>
 */
package xyz.sunqian.common.runtime.proxy;