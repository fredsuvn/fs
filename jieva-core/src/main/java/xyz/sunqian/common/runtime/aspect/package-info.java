/**
 * Provides makers to rapidly create aspect class with minimal effort, using code similar to the following:
 * <pre>{@code
 * AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
 * AdvisedClass ac = spec.newInstance();
 * }</pre>
 * <p>
 * Core interfaces:
 * <ul>
 *     <li>{@link xyz.sunqian.common.runtime.aspect.AspectMaker}</li>
 *     <li>{@link xyz.sunqian.common.runtime.aspect.AspectSpec}</li>
 *     <li>{@link xyz.sunqian.common.runtime.aspect.AspectHandler}</li>
 * </ul>
 */
package xyz.sunqian.common.runtime.aspect;