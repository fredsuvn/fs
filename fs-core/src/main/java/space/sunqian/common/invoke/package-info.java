/**
 * Provides unified interfaces to handle an invocable body, for example:
 * <pre>{@code
 * Invocable byDefault = Invocable.of(method);
 * Invocable byAsm = Invocable.of(constructor, InvocationMode.ASM);
 * }</pre>
 * <p>
 * Utilities:
 * <ul>
 *     <li>{@link space.sunqian.common.invoke.InvokeKit}</li>
 * </ul>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.common.invoke.Invocable}</li>
 * </ul>
 */
package space.sunqian.common.invoke;