package xyz.sunqian.common.runtime.reflect;

import xyz.sunqian.annotations.Nonnull;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Objects;

final class AssignBack {

    public static boolean isAssignable(@Nonnull Type assigned, @Nonnull Type assignee) {
        if (assigned instanceof Class<?>) {
            if (Objects.equals(assigned, assignee) || Objects.equals(assigned, Object.class)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return ((Class<?>) assigned).isAssignableFrom((Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignable((Class<?>) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignable(assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignable(assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignable((Class<?>) assigned, (GenericArrayType) assignee);
            }
        } else if (assigned instanceof ParameterizedType) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return isAssignable((ParameterizedType) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignable((ParameterizedType) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignable(assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignable(assigned, (TypeVariable<?>) assignee);
            }
            // if (assignee instanceof GenericArrayType) {
            //     return false;
            // }
        } else if (assigned instanceof WildcardType) {
            return isAssignable((WildcardType) assigned, assignee);
        } else if (assigned instanceof TypeVariable<?>) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            // if (assignee instanceof Class<?>) {
            //     return isAssignable((TypeVariable<?>) assigned, (Class<?>) assignee);
            // }
            // if (assignee instanceof ParameterizedType) {
            //     return isAssignable((TypeVariable<?>) assigned, (ParameterizedType) assignee);
            // }
            if (assignee instanceof WildcardType) {
                return isAssignable(assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignable(assigned, (TypeVariable<?>) assignee);
            }
            // if (assignee instanceof GenericArrayType) {
            //     return isAssignable((TypeVariable<?>) assigned, (GenericArrayType) assignee);
            // }
        } else if (assigned instanceof GenericArrayType) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return isAssignable((GenericArrayType) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignable((GenericArrayType) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignable(assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignable(assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignable((GenericArrayType) assigned, (GenericArrayType) assignee);
            }
        }
        return false;
    }

    private static boolean isAssignable(@Nonnull Class<?> assigned, @Nonnull ParameterizedType assignee) {
        Type assigneeRaw = assignee.getRawType();
        if (!(assigneeRaw instanceof Class<?>)) {
            return false;
        }
        return assigned.isAssignableFrom((Class<?>) assigneeRaw);
    }

    private static boolean isAssignable(@Nonnull Class<?> assigned, @Nonnull GenericArrayType assignee) {
        if (!assigned.isArray()) {
            return false;
        }
        Type assignedComponent = assigned.getComponentType();
        Type assigneeComponent = assignee.getGenericComponentType();
        return isAssignable(assignedComponent, assigneeComponent);
    }

    private static boolean isAssignable(@Nonnull ParameterizedType assigned, @Nonnull Class<?> assignee) {
        Class<?> assignedRaw = (Class<?>) assigned.getRawType();
        return assignedRaw.isAssignableFrom(assignee);
    }

    private static boolean isAssignable(@Nonnull ParameterizedType assigned, @Nonnull ParameterizedType assignee) {
        Type assignedRaw = assigned.getRawType();
        if (!(assignedRaw instanceof Class<?>)) {
            return false;
        }
        Type assigneeRaw = assignee.getRawType();
        if (!(assigneeRaw instanceof Class<?>)) {
            return false;
        }
        if (!((Class<?>) assignedRaw).isAssignableFrom((Class<?>) assigneeRaw)) {
            return false;
        }
        List<Type> assigneeArgs = TypeKit.resolveActualTypeArguments(assignee, (Class<?>) assignedRaw);
        Type[] assignedArgs = assigned.getActualTypeArguments();
        if (assignedArgs.length != assigneeArgs.size()) {
            return false;
        }
        for (int i = 0; i < assignedArgs.length; i++) {
            Type assignedArg = assignedArgs[i];
            Type assigneeArg = assigneeArgs.get(i);
            if (assignedArg instanceof WildcardType) {
                if (!isAssignableParameterizedArgs((WildcardType) assignedArg, assigneeArg)) {
                    return false;
                }
                continue;
            }
            if (!Objects.equals(assignedArg, assigneeArg)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAssignableParameterizedArgs(@Nonnull WildcardType assignedArg, @Nonnull Type assigneeArg) {
        Type assignedUpper = TypeKit.getUpperBound(assignedArg);
        if (!isAssignable(assignedUpper, assigneeArg)) {
            return false;
        }
        Type assignedLower = TypeKit.getLowerBound(assignedArg);
        return assignedLower == null || isAssignable(assigneeArg, assignedLower);
    }

    private static boolean isAssignable(@Nonnull WildcardType assigned, @Nonnull Type assignee) {
        Type lowerType = TypeKit.getLowerBound(assigned);
        if (lowerType != null) {
            return isAssignable(lowerType, assignee);
        }
        return false;
    }

    private static boolean isAssignable(@Nonnull Type assigned, @Nonnull WildcardType assignee) {
        Type assigneeUpper = TypeKit.getUpperBound(assignee);
        return isAssignable(assigned, assigneeUpper);
    }

    private static boolean isAssignable(@Nonnull Type assigned, @Nonnull TypeVariable<?> assignee) {
        Type[] assigneeUppers = assignee.getBounds();
        for (Type assigneeUpper : assigneeUppers) {
            if (isAssignable(assigned, assigneeUpper)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAssignable(@Nonnull GenericArrayType assigned, @Nonnull Class<?> assignee) {
        if (!assignee.isArray()) {
            return false;
        }
        Type assignedComponent = assigned.getGenericComponentType();
        Type assigneeComponent = assignee.getComponentType();
        return isAssignable(assignedComponent, assigneeComponent);
    }

    private static boolean isAssignable(@Nonnull GenericArrayType assigned, @Nonnull ParameterizedType assignee) {
        return false;
    }

    private static boolean isAssignable(@Nonnull GenericArrayType assigned, @Nonnull GenericArrayType assignee) {
        Type assignedComponent = assigned.getGenericComponentType();
        Type assigneeComponent = assignee.getGenericComponentType();
        return isAssignable(assignedComponent, assigneeComponent);
    }
}
