package space.sunqian.common.runtime.aspect;

import space.sunqian.annotations.Nonnull;
import space.sunqian.annotations.Nullable;

import java.lang.reflect.Method;

/**
 * The AspectHandler is an interface used to handle aspect-oriented programming, including determining whether a method
 * needs to apply aspect ({@link #needsAspect(Method)}) and the specific behavior of the target advised method
 * ({@link #beforeInvoking(Method, Object[], Object)}, {@link #afterReturning(Object, Method, Object[], Object)} and
 * {@link #afterThrowing(Throwable, Method, Object[], Object)}).
 * <p>
 * This interface is typically used for {@link AspectMaker#make(Class, AspectHandler)}.
 *
 * @author sunqian
 */
public interface AspectHandler {

    /**
     * Returns whether the aspect-oriented programming should be applied to the given method.
     * <p>
     * Typically, the {@link AspectMaker} invokes this method for each aspect-able method of the class which applies the
     * aspect, only once.
     *
     * @param method the given method
     * @return whether the aspect-oriented programming should be applied to the given method
     */
    boolean needsAspect(@Nonnull Method method);

    /**
     * This method will be invoked before the invocation of the target method.
     *
     * @param method the target method being intercepted (advised)
     * @param args   the arguments passed to the target method, changes to the array elements will propagate to the
     *               target method
     * @param target the target object which the advised method belongs to, may be a proxy instance itself
     * @throws Throwable any exception thrown by the invocation
     */
    void beforeInvoking(
        @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target
    ) throws Throwable;

    /**
     * This method will be invoked after the invocation of the target method. The original return value of the target
     * method will be replaced by the returned value of this method as the final return value.
     *
     * @param result the original result of the invocation of the target method, may be {@code null}
     * @param method the target method being intercepted (advised)
     * @param args   the arguments passed to the target method, changes to the array elements will propagate to the
     *               {@link #afterThrowing(Throwable, Method, Object[], Object)}
     * @param target the target object which the advised method belongs to, may be a proxy instance itself
     * @return the final return value
     * @throws Throwable any exception thrown by the invocation
     */
    @Nullable
    Object afterReturning(
        @Nullable Object result, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target
    ) throws Throwable;

    /**
     * This method is used to handle the exception thrown by all pre-invocations, including the invocation of the target
     * method, {@link #beforeInvoking(Method, Object[], Object)} and
     * {@link #afterReturning(Object, Method, Object[], Object)}. The return value of this method will be the final
     * return value.
     *
     * @param ex     the exception thrown by all pre-invocations
     * @param method the target method being intercepted (advised)
     * @param args   the arguments passed to the target method, may be changed by pre-invocations
     * @param target the target object which the advised method belongs to, may be a proxy object
     * @return the final return value
     */
    @Nullable
    Object afterThrowing(
        @Nonnull Throwable ex, @Nonnull Method method, Object @Nonnull [] args, @Nonnull Object target
    );
}
