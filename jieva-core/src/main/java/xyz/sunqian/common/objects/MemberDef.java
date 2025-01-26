package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * This is the super interface for member definitions in {@link ObjectDef}, such as {@link PropertyDef} and
 * {@link MethodDef}.
 *
 * @author sunqian
 */
@Immutable
@ThreadSafe
public interface MemberDef {

    /**
     * Returns owner {@link ObjectDef} of this member.
     *
     * @return owner {@link ObjectDef} of this member
     */
    ObjectDef getOwner();

    /**
     * Returns name of this member.
     *
     * @return name of this member
     */
    String getName();

    /**
     * Returns annotations on this member, may be empty if none exist.
     *
     * @return annotations on this member, may be empty if none exist
     */
    List<Annotation> getAnnotations();

    /**
     * Returns annotation of specified type on this member, or null if it doesn't exist. The search behavior is
     * equivalent to that in the {@link #getAnnotations()}.
     *
     * @param type specified type
     * @param <A>  type of annotation
     * @return annotation of specified type on this member, or null if it doesn't exist
     */
    @Nullable
    <A extends Annotation> A getAnnotation(Class<A> type);
}
