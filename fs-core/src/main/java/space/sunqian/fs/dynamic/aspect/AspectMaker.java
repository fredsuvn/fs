package space.sunqian.fs.dynamic.aspect;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.dynamic.aspect.asm.AsmAspectMaker;

/**
 * This interface is used to create aspect class to implement aspect-oriented programming. The implementations should be
 * thread-safe.
 *
 * @author sunqian
 */
@ThreadSafe
public interface AspectMaker {

    /**
     * Returns an instance of {@link AsmAspectMaker} which implements the {@link AspectMaker}.
     *
     * @return an instance of {@link AsmAspectMaker} which implements the {@link AspectMaker}
     */
    static @Nonnull AsmAspectMaker byAsm() {
        return new AsmAspectMaker();
    }

    /**
     * Creates a new {@link AspectSpec} instance to create aspect instances, with the advised class and specified aspect
     * handler.
     *
     * @param advisedClass  the specified class to be advised to implement aspect-oriented programming
     * @param aspectHandler the specified aspect handler
     * @return a new {@link AspectSpec} instance to create aspect instances
     * @throws AspectException if any problem occurs during creation
     */
    @Nonnull
    AspectSpec make(
        @Nonnull Class<?> advisedClass,
        @Nonnull AspectHandler aspectHandler
    ) throws AspectException;
}
