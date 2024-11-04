package xyz.sunqian.common.bean;

import xyz.sunqian.annotations.Immutable;

/**
 * Information about the method of {@link BeanInfo}, commonly using {@link BeanInfo#getMethods()} to get the instance.
 *
 * @author fredsuvn
 * @see BeanInfo
 */
@Immutable
public interface MethodInfo extends MemberInfo, BaseMethodInfo {
}
