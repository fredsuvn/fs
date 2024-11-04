package xyz.sunqian.common.bean;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Jie;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Objects;

/**
 * Common information about the member of {@link BeanInfo} such as {@link PropertyInfo} and {@link MethodInfo}.
 *
 * @author fredsuvn
 * @see BeanInfo
 * @see PropertyInfo
 * @see MethodInfo
 */
@Immutable
@ThreadSafe
public interface MemberInfo {

    /**
     * Returns owner bean info of this info.
     *
     * @return owner bean info of this info
     */
    BeanInfo getOwner();

    /**
     * Returns name of this member.
     *
     * @return name of this member
     */
    String getName();

    /**
     * Returns annotations on this member, may be empty if there is no.
     *
     * @return annotations on this member
     */
    List<Annotation> getAnnotations();

    /**
     * Returns annotation of specified type in order of {@link #getAnnotations()}, may be null if not found.
     *
     * @param annotationType specified type
     * @param <A>            type of annotation
     * @return annotation of specified type, may be null if not found
     */
    @Nullable
    default <A extends Annotation> A getAnnotation(Class<A> annotationType) {
        for (Annotation annotation : getAnnotations()) {
            if (Objects.equals(annotationType, annotation.annotationType())) {
                return Jie.as(annotation);
            }
        }
        return null;
    }
}
