package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This interface represents the introspection info for an object method, and typically provides introspection functions
 * for generating {@link MethodDef}.
 *
 * @author sunqian
 */
@Immutable
public interface MethodIntro {

    /**
     * Returns name of this method.
     *
     * @return name of this method
     */
    String getName();

    /**
     * Invokes this method with specified instance and arguments, returns the result.
     *
     * @param inst specified instance
     * @param args specified arguments
     * @return the result
     */
    Object invoke(Object inst, Object... args);

    /**
     * Returns the java {@link Method} backing this method.
     *
     * @return the java {@link Method} backing this method
     */
    Method getMethod();

    /**
     * Returns annotations on this method, may be empty if none exist.
     *
     * @return annotations on this method, may be empty if none exist
     */
    List<Annotation> getAnnotations();

    /**
     * Returns annotation of specified type on this method, or null if it doesn't exist. The search behavior is
     * equivalent to that in the {@link #getAnnotations()}.
     *
     * @param type specified type
     * @param <A>  type of annotation
     * @return annotation of specified type on this method, or null if it doesn't exist
     */
    @Nullable
    <A extends Annotation> A getAnnotation(Class<A> type);
}
