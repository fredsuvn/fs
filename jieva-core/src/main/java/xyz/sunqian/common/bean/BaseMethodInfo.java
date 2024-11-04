package xyz.sunqian.common.bean;

import xyz.sunqian.annotations.Immutable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Base info of {@link MemberInfo}.
 *
 * @author fredsuvn
 */
@Immutable
public interface BaseMethodInfo {

    /**
     * Returns name of this method.
     *
     * @return name of this method
     */
    String getName();

    /**
     * Invokes this method and returns result.
     *
     * @param bean bean object
     * @param args method arguments
     * @return the result
     */
    Object invoke(Object bean, Object... args);

    /**
     * Returns java method of this bean method.
     *
     * @return java method of this bean method
     */
    Method getMethod();

    /**
     * Returns annotations on this method, may be empty if there is no.
     *
     * @return annotations on this method
     */
    List<Annotation> getAnnotations();
}
