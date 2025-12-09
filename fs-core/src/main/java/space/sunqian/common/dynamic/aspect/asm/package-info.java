/**
 * Provides aspect maker implementations by ASM, using codes similar to the following:
 * <pre>{@code
 * AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
 * AdvisedClass ac = spec.newInstance();
 * }</pre>
 * Core implementations:
 * <ul>
 *     <li>{@link space.sunqian.common.dynamic.aspect.asm.AsmAspectMaker}</li>
 * </ul>
 */
package space.sunqian.common.dynamic.aspect.asm;