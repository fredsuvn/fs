package space.sunqian.fs.reflect;

import space.sunqian.annotation.Nonnull;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.List;
import java.util.Objects;

final class AssignBack {

    public static boolean isAssignable(@Nonnull Type assigned, @Nonnull Type assignee, boolean rawCompatible) {
        if (assigned instanceof Class<?>) {
            if (Objects.equals(assigned, assignee) || Objects.equals(assigned, Object.class)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return ((Class<?>) assigned).isAssignableFrom((Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignable((Class<?>) assigned, (ParameterizedType) assignee, rawCompatible);
            }
            if (assignee instanceof WildcardType) {
                return isAssignable(assigned, (WildcardType) assignee, rawCompatible);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignable(assigned, (TypeVariable<?>) assignee, rawCompatible);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignable((Class<?>) assigned, (GenericArrayType) assignee, rawCompatible);
            }
        } else if (assigned instanceof ParameterizedType) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return isAssignable((ParameterizedType) assigned, (Class<?>) assignee, rawCompatible);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignable((ParameterizedType) assigned, (ParameterizedType) assignee, rawCompatible);
            }
            if (assignee instanceof WildcardType) {
                return isAssignable(assigned, (WildcardType) assignee, rawCompatible);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignable(assigned, (TypeVariable<?>) assignee, rawCompatible);
            }
            // if (assignee instanceof GenericArrayType) {
            //     return false;
            // }
        } else if (assigned instanceof WildcardType) {
            return isAssignable((WildcardType) assigned, assignee, rawCompatible);
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
                return isAssignable(assigned, (WildcardType) assignee, rawCompatible);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignable(assigned, (TypeVariable<?>) assignee, rawCompatible);
            }
            // if (assignee instanceof GenericArrayType) {
            //     return isAssignable((TypeVariable<?>) assigned, (GenericArrayType) assignee);
            // }
        } else if (assigned instanceof GenericArrayType) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return isAssignable((GenericArrayType) assigned, (Class<?>) assignee, rawCompatible);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignable((GenericArrayType) assigned, (ParameterizedType) assignee, rawCompatible);
            }
            if (assignee instanceof WildcardType) {
                return isAssignable(assigned, (WildcardType) assignee, rawCompatible);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignable(assigned, (TypeVariable<?>) assignee, rawCompatible);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignable((GenericArrayType) assigned, (GenericArrayType) assignee, rawCompatible);
            }
        }
        return false;
    }

    private static boolean isAssignable(@Nonnull Class<?> assigned, @Nonnull ParameterizedType assignee, boolean rawCompatible) {
        Type assigneeRaw = assignee.getRawType();
        if (!(assigneeRaw instanceof Class<?>)) {
            return false;
        }
        return assigned.isAssignableFrom((Class<?>) assigneeRaw);
    }

    private static boolean isAssignable(@Nonnull Class<?> assigned, @Nonnull GenericArrayType assignee, boolean rawCompatible) {
        if (!assigned.isArray()) {
            return false;
        }
        Type assignedComponent = assigned.getComponentType();
        Type assigneeComponent = assignee.getGenericComponentType();
        return isAssignable(assignedComponent, assigneeComponent, rawCompatible);
    }

    private static boolean isAssignable(@Nonnull ParameterizedType assigned, @Nonnull Class<?> assignee, boolean rawCompatible) {
        if (!rawCompatible) {
            return false;
        }
        Class<?> assignedRaw = (Class<?>) assigned.getRawType();
        if (!assignedRaw.isAssignableFrom(assignee)) {
            return false;
        }
        List<Type> assigneeArgs = TypeKit.resolveActualTypeArguments(assignee, assignedRaw);
        if (isAllTypeVariables(assigneeArgs)) {
            return true;
        }
        ParameterizedType assigneeParam = TypeKit.parameterizedType(assignedRaw, assigneeArgs.toArray(new Type[0]));
        return isAssignable(assigned, assigneeParam, rawCompatible);
    }

    private static boolean isAllTypeVariables(List<Type> types) {
        for (Type type : types) {
            if (!(type instanceof TypeVariable<?>)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isAssignable(@Nonnull ParameterizedType assigned, @Nonnull ParameterizedType assignee, boolean rawCompatible) {
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
                if (!isAssignableParameterizedArgs((WildcardType) assignedArg, assigneeArg, rawCompatible)) {
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

    private static boolean isAssignableParameterizedArgs(@Nonnull WildcardType assignedArg, @Nonnull Type assigneeArg, boolean rawCompatible) {
        Type assignedUpper = TypeKit.getUpperBound(assignedArg);
        if (!isAssignable(assignedUpper, assigneeArg, rawCompatible)) {
            return false;
        }
        Type assignedLower = TypeKit.getLowerBound(assignedArg);
        return assignedLower == null || isAssignable(assigneeArg, assignedLower, rawCompatible);
    }

    private static boolean isAssignable(@Nonnull WildcardType assigned, @Nonnull Type assignee, boolean rawCompatible) {
        Type lowerType = TypeKit.getLowerBound(assigned);
        if (lowerType != null) {
            return isAssignable(lowerType, assignee, rawCompatible);
        }
        return false;
    }

    private static boolean isAssignable(@Nonnull Type assigned, @Nonnull WildcardType assignee, boolean rawCompatible) {
        Type assigneeUpper = TypeKit.getUpperBound(assignee);
        return isAssignable(assigned, assigneeUpper, rawCompatible);
    }

    private static boolean isAssignable(@Nonnull Type assigned, @Nonnull TypeVariable<?> assignee, boolean rawCompatible) {
        Type[] assigneeUppers = assignee.getBounds();
        for (Type assigneeUpper : assigneeUppers) {
            if (isAssignable(assigned, assigneeUpper, rawCompatible)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isAssignable(@Nonnull GenericArrayType assigned, @Nonnull Class<?> assignee, boolean rawCompatible) {
        if (!assignee.isArray()) {
            return false;
        }
        Type assignedComponent = assigned.getGenericComponentType();
        Type assigneeComponent = assignee.getComponentType();
        return isAssignable(assignedComponent, assigneeComponent, rawCompatible);
    }

    private static boolean isAssignable(@Nonnull GenericArrayType assigned, @Nonnull ParameterizedType assignee, boolean rawCompatible) {
        return false;
    }

    private static boolean isAssignable(@Nonnull GenericArrayType assigned, @Nonnull GenericArrayType assignee, boolean rawCompatible) {
        Type assignedComponent = assigned.getGenericComponentType();
        Type assigneeComponent = assignee.getGenericComponentType();
        return isAssignable(assignedComponent, assigneeComponent, rawCompatible);
    }

    private AssignBack() {
    }
}
