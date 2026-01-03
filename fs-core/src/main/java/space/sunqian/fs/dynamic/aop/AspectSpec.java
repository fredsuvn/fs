package space.sunqian.fs.dynamic.aop;

import space.sunqian.annotation.Nonnull;

/**
 * The specification of an aspect class, provides definition info of the aspect class and methods to create its
 * instances.
 *
 * @author sunqian
 */
public interface AspectSpec {

    /**
     * Creates and returns a new instance of the aspect class.
     *
     * @param <T> the type of the instance
     * @return a new instance of the aspect class
     * @throws AspectException if any problem occurs
     */
    <T> @Nonnull T newInstance() throws AspectException;

    /**
     * Returns the aspect class defined by this specification.
     *
     * @return the aspect class defined by this specification
     */
    @Nonnull
    Class<?> aspectClass();

    /**
     * Returns the advised class, which is typically the superclass of the {@link #aspectClass()}.
     *
     * @return the advised class, which is typically the superclass of the {@link #aspectClass()}
     */
    @Nonnull
    Class<?> advisedClass();

    /**
     * Returns the aspect handler, which is used to handle the aspect behavior. The handler typically is the handler
     * argument of {@link AspectMaker#make(Class, AspectHandler)}.
     *
     * @return the aspect handler, which is used to handle the aspect behavior
     */
    @Nonnull
    AspectHandler aspectHandler();
}
