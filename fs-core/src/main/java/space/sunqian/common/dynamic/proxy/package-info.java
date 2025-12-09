/**
 * Provides makers to rapidly create proxy class with minimal effort, using codes similar to the following:
 * <pre>{@code
 * ProxySpec spec = ProxyMaker.byAsm().make(cls, interfaces, handler);
 * ProxyClass ac = spec.newInstance();
 * }</pre>
 * <p>
 * Utilities:
 * <ul>
 *     <li>{@link space.sunqian.common.dynamic.proxy.ProxyKit}</li>
 * </ul>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.common.dynamic.proxy.ProxyMaker}</li>
 *     <li>{@link space.sunqian.common.dynamic.proxy.ProxySpec}</li>
 *     <li>{@link space.sunqian.common.dynamic.proxy.ProxyHandler}</li>
 *     <li>{@link space.sunqian.common.dynamic.proxy.ProxyInvoker}</li>
 * </ul>
 */
package space.sunqian.common.dynamic.proxy;