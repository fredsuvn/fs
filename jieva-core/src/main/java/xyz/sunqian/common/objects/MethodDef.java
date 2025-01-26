package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;

import java.beans.MethodDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This interface represents the definition of an object method and forms part of {@link ObjectDef}.
 * <p>
 * It is very similar to {@link MethodDescriptor}, which describes method of
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>, but the introspection
 * rules is determined by {@link ObjectDef#getIntrospector()}.
 *
 * @author sunqian
 */
@Immutable
public interface MethodDef extends MemberDef, MethodIntro {

    /**
     * Returns owner {@link ObjectDef} of this method.
     *
     * @return owner {@link ObjectDef} of this method
     */
    ObjectDef getOwner();

    /**
     * Returns name of this method.
     *
     * @return name of this method
     */
    String getName();

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
