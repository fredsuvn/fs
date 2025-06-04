package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nonnull;

import java.lang.invoke.MethodHandle;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * This enum indicates the way to implement {@link Invocable}.
 *
 * @author sunqian
 */
public enum InvocationMode implements InvocableGenerator {

    /**
     * Using reflection to implement {@link Invocable}.
     */
    REFLECT(new OfReflection()),

    /**
     * Using {@link MethodHandle} to implement {@link Invocable}.
     */
    METHOD_HANDLE(new OfMethodHandle()),

    /**
     * Using <a href="https://asm.ow2.io/">ASM</a> to implement {@link Invocable}.
     */
    ASM(new OfAsm()),
    ;

    static final InvocationMode DEFAULT = METHOD_HANDLE;

    private final @Nonnull InvocableGenerator generator;

    InvocationMode(@Nonnull InvocableGenerator generator) {
        this.generator = generator;
    }

    public @Nonnull Invocable generate(@Nonnull Method method) {
        return generator.generate(method);
    }

    public @Nonnull Invocable generate(@Nonnull Constructor<?> constructor) {
        return generator.generate(constructor);
    }
}
