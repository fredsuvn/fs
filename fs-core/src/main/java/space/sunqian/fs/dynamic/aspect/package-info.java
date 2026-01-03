/**
 * Provides makers to rapidly create aspect class with minimal effort, using codes similar to the following:
 * <pre>{@code
 * AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
 * AdvisedClass ac = spec.newInstance();
 * }</pre>
 * <p>
 * Core interfaces:
 * <ul>
 *     <li>{@link space.sunqian.fs.dynamic.aspect.AspectMaker}</li>
 *     <li>{@link space.sunqian.fs.dynamic.aspect.AspectSpec}</li>
 *     <li>{@link space.sunqian.fs.dynamic.aspect.AspectHandler}</li>
 * </ul>
 */
package space.sunqian.fs.dynamic.aspect;