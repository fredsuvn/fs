/**
 * Provides proxy maker implementations by JDK, using codes similar to the following:
 * <pre>{@code
 * ProxySpec spec = ProxyMaker.byJdk().make(cls, interfaces, handler);
 * ProxyClass ac = spec.newInstance();
 * }</pre>
 * Core implementations:
 * <ul>
 *     <li>{@link space.sunqian.common.dynamic.proxy.jdk.JdkProxyMaker}</li>
 * </ul>
 */
package space.sunqian.common.dynamic.proxy.jdk;