/**
 * Provides aspect maker implementations by ASM, using code similar to the following:
 * <pre>{@code
 * AspectSpec spec = AspectMaker.byAsm().make(cls, handler);
 * AdvisedClass ac = spec.newInstance();
 * }</pre>
 * Core implementations:
 * <ul>
 *     <li>{@link space.sunqian.common.runtime.aspect.asm.AsmAspectMaker}</li>
 * </ul>
 */
package space.sunqian.common.runtime.aspect.asm;