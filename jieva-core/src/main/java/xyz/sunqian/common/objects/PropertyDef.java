package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This interface represents the definition of an object property and forms part of {@link ObjectDef}.
 * <p>
 * It is very similar to {@link PropertyDescriptor}, which describes property of
 * <a href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a>, but the introspection
 * rules is determined by {@link ObjectDef#getIntrospector()}.
 *
 * @author sunqian
 */
@Immutable
public interface PropertyDef extends MemberDef, PropertyIntro {

    /**
     * Returns owner {@link ObjectDef} of this property.
     *
     * @return owner {@link ObjectDef} of this property
     */
    ObjectDef getOwner();

    /**
     * Returns name of this property.
     *
     * @return name of this property
     */
    String getName();

    /**
     * Returns annotations on this property, including those on the getter method, setter method and field backing this
     * property, in that order. It is similar to a combination of {@link #getGetterAnnotations()},
     * {@link #getSetterAnnotations()}, and {@link #getFieldAnnotations()}.
     *
     * @return annotations on this property, may be empty if none exist
     */
    List<Annotation> getAnnotations();

    /**
     * Returns annotation of specified type on this property, or null if it doesn't exist. The search behavior is
     * equivalent to that in the {@link #getAnnotations()}.
     *
     * @param type specified type
     * @param <A>  type of annotation
     * @return annotation of specified type on this property, or null if it doesn't exist
     */
    @Nullable
    <A extends Annotation> A getAnnotation(Class<A> type);
}
