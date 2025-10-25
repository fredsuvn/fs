/**
 * Provides unified interfaces to handle an invocable body, for example:
 * <pre>{@code
 * Invocable byDefault = Invocable.of(method);
 * Invocable byAsm = Invocable.of(constructor, InvocationMode.ASM);
 * }</pre>
 * <p>
 * Utilities:
 * <ul>
 *     <li>{@link space.sunqian.common.runtime.invoke.InvokeKit}</li>
 * </ul>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.common.runtime.invoke.Invocable}</li>
 * </ul>
 */
package space.sunqian.common.runtime.invoke;