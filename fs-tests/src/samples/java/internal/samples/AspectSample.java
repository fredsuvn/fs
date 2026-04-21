package internal.samples;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.dynamic.aspect.AspectHandler;
import space.sunqian.fs.dynamic.aspect.AspectMaker;

import java.lang.reflect.Method;

/**
 * Sample: Aspect-Oriented Programming Usage
 * <p>
 * Purpose: Demonstrate how to use the aspect-oriented programming utilities provided by fs-core module.
 * <p>
 * Use Cases:
 * <ul>
 *   <li>
 *     Create aspect-enhanced classes
 *   </li>
 *   <li>
 *     Intercept method calls with before/after logic
 *   </li>
 *   <li>
 *     Handle exceptions in methods
 *   </li>
 * </ul>
 * <p>
 * Key Classes:
 * <ul>
 *   <li>
 *     {@link AspectMaker}: Creates aspect-enhanced classes using ASM
 *   </li>
 *   <li>
 *     {@link AspectHandler}: Handles aspect interceptions
 *   </li>
 * </ul>
 */
public class AspectSample {

    public static void main(String[] args) {
        demonstrateAspectOrientedProgramming();
    }

    /**
     * Demonstrates creating and using aspect-oriented programming.
     */
    public static void demonstrateAspectOrientedProgramming() {
        System.out.println("=== Aspect-Oriented Programming Demonstration ===");

        // Create aspect-enhanced Hello class
        Hello aspect = AspectMaker.byAsm().make(Hello.class, new AspectHandler() {

            @Override
            public boolean needsAspect(@Nonnull Method method) {
                return method.getName().equals("hello");
            }

            @Override
            public void beforeInvoking(@Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                System.out.println("Aspect: Before invoking hello()");
            }

            @Override
            public @Nonnull Object afterReturning(@Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) throws Throwable {
                System.out.println("Aspect: After returning from hello()");
                return result + "[aspect]";
            }

            @Override
            public @Nullable Object afterThrowing(@Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target) {
                System.out.println("Aspect: After throwing exception");
                return null;
            }
        }).newInstance();

        // Call method on aspect-enhanced object
        String aspectResult = aspect.hello();
        System.out.println("Aspect result: " + aspectResult);
    }

    /**
     * Sample class for aspect demonstration.
     */
    public static class Hello {

        public String hello() {
            System.out.println("Hello.hello() called");
            return "hello";
        }
    }
}