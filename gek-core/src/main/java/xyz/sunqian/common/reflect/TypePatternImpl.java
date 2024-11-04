package xyz.sunqian.common.reflect;

import java.lang.reflect.*;
import java.util.Objects;

final class TypePatternImpl implements TypePattern {

    static final TypePatternImpl INSTANCE = new TypePatternImpl();

    @Override
    public boolean isAssignableClassToClass(Class<?> assigned, Class<?> assignee) {
        return assigned.isAssignableFrom(assignee);
    }

    @Override
    public boolean isAssignableClassToParameterized(Class<?> assigned, ParameterizedType assignee) {
        if (Objects.equals(assigned, Object.class)) {
            return true;
        }
        Class<?> assigneeRaw = (Class<?>) assignee.getRawType();
        return assigned.isAssignableFrom(assigneeRaw);
    }

    @Override
    public boolean isAssignableClassToWildcard(Class<?> assigned, WildcardType assignee) {
        if (Objects.equals(assigned, Object.class)) {
            return true;
        }
        return isAssignableToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableClassToTypeVariable(Class<?> assigned, TypeVariable<?> assignee) {
        if (Objects.equals(assigned, Object.class)) {
            return true;
        }
        return isAssignableToTypeVariable(assigned, assignee);
    }

    @Override
    public boolean isAssignableClassToGenericArray(Class<?> assigned, GenericArrayType assignee) {
        if (Objects.equals(assigned, Object.class)) {
            return true;
        }
        if (!assigned.isArray()) {
            return false;
        }
        Type assignedComponent = assigned.getComponentType();
        Type assigneeComponent = assignee.getGenericComponentType();
        return isAssignable(assignedComponent, assigneeComponent);
    }

    @Override
    public boolean isAssignableParameterizedToClass(ParameterizedType assigned, Class<?> assignee) {
        Class<?> assignedRaw = (Class<?>) assigned.getRawType();
        return assignedRaw.isAssignableFrom(assignee);
    }

    @Override
    public boolean isAssignableParameterizedToParameterized(ParameterizedType assigned, ParameterizedType assignee) {
        Class<?> assignedRaw = (Class<?>) assigned.getRawType();
        Class<?> assigneeRaw = (Class<?>) assignee.getRawType();
        if (!assignedRaw.isAssignableFrom(assigneeRaw)) {
            return false;
        }
        Type[] assignedArgs = assigned.getActualTypeArguments();
        Type[] assigneeArgs = assignee.getActualTypeArguments();
        if (assignedArgs.length != assigneeArgs.length) {
            return false;
        }
        for (int i = 0; i < assignedArgs.length; i++) {
            Type assignedArg = assignedArgs[i];
            Type assigneeArg = assigneeArgs[i];
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

    private boolean isAssignableParameterizedArgs(WildcardType assignedArg, Type assigneeArg) {
        if (assigneeArg instanceof WildcardType) {
            Type assignedUpper = JieReflect.getUpperBound(assignedArg);
            Type assigneeUpper = JieReflect.getUpperBound((WildcardType) assigneeArg);
            if (!isAssignable(assignedUpper, assigneeUpper)) {
                return false;
            }
            Type assignedLower = JieReflect.getLowerBound(assignedArg);
            if (assignedLower == null) {
                return true;
            }
            Type assigneeLower = JieReflect.getLowerBound((WildcardType) assigneeArg);
            if (assigneeLower == null) {
                return false;
            }
            return isAssignable(assigneeLower, assignedLower);
        }
        if (assigneeArg instanceof TypeVariable<?>) {
            Type assignedLower = JieReflect.getLowerBound(assignedArg);
            if (assignedLower != null) {
                return isAssignable(assigneeArg, assignedLower);
            }
            Type assignedUpper = JieReflect.getUpperBound(assignedArg);
            return isAssignable(assignedUpper, assigneeArg);
        }
        Type assignedUpper = JieReflect.getUpperBound(assignedArg);
        if (!isAssignable(assignedUpper, assigneeArg)) {
            return false;
        }
        Type assignedLower = JieReflect.getLowerBound(assignedArg);
        return assignedLower == null || isAssignable(assigneeArg, assignedLower);
    }

    @Override
    public boolean isAssignableParameterizedToWildcard(ParameterizedType assigned, WildcardType assignee) {
        return isAssignableToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableParameterizedToTypeVariable(ParameterizedType assigned, TypeVariable<?> assignee) {
        return isAssignableToTypeVariable(assigned, assignee);
    }

    @Override
    public boolean isAssignableParameterizedToGenericArray(ParameterizedType assigned, GenericArrayType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableWildcardToClass(WildcardType assigned, Class<?> assignee) {
        return isAssignableWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableWildcardToParameterized(WildcardType assigned, ParameterizedType assignee) {
        return isAssignableWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableWildcardToWildcard(WildcardType assigned, WildcardType assignee) {
        return isAssignableWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableWildcardToTypeVariable(WildcardType assigned, TypeVariable<?> assignee) {
        return isAssignableWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableWildcardToGenericArray(WildcardType assigned, GenericArrayType assignee) {
        return isAssignableWildcard(assigned, assignee);
    }

    private boolean isAssignableWildcard(WildcardType assigned, Type assignee) {
        Type lowerType = JieReflect.getLowerBound(assigned);
        if (lowerType != null) {
            return isAssignable(lowerType, assignee);
        }
        return false;
    }

    @Override
    public boolean isAssignableTypeVariableToClass(TypeVariable<?> assigned, Class<?> assignee) {
        return false;
    }

    @Override
    public boolean isAssignableTypeVariableToParameterized(TypeVariable<?> assigned, ParameterizedType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableTypeVariableToWildcard(TypeVariable<?> assigned, WildcardType assignee) {
        return isAssignableToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableTypeVariableToTypeVariable(TypeVariable<?> assigned, TypeVariable<?> assignee) {
        Type[] assigneeUppers = assignee.getBounds();
        for (Type assigneeUpper : assigneeUppers) {
            if (isAssignable(assigned, assigneeUpper)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAssignableTypeVariableToGenericArray(TypeVariable<?> assigned, GenericArrayType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableGenericArrayToClass(GenericArrayType assigned, Class<?> assignee) {
        if (!assignee.isArray()) {
            return false;
        }
        Type assignedComponent = assigned.getGenericComponentType();
        Type assigneeComponent = assignee.getComponentType();
        return isAssignable(assignedComponent, assigneeComponent);
    }

    @Override
    public boolean isAssignableGenericArrayToParameterized(GenericArrayType assigned, ParameterizedType assignee) {
        return false;
    }

    @Override
    public boolean isAssignableGenericArrayToWildcard(GenericArrayType assigned, WildcardType assignee) {
        return isAssignableToWildcard(assigned, assignee);
    }

    @Override
    public boolean isAssignableGenericArrayToTypeVariable(GenericArrayType assigned, TypeVariable<?> assignee) {
        return isAssignableToTypeVariable(assigned, assignee);
    }

    @Override
    public boolean isAssignableGenericArrayToGenericArray(GenericArrayType assigned, GenericArrayType assignee) {
        Type assignedComponent = assigned.getGenericComponentType();
        Type assigneeComponent = assignee.getGenericComponentType();
        return isAssignable(assignedComponent, assigneeComponent);
    }

    public boolean isAssignableToWildcard(Type assigned, WildcardType assignee) {
        Type assigneeUpper = JieReflect.getUpperBound(assignee);
        return isAssignable(assigned, assigneeUpper);
    }

    public boolean isAssignableToTypeVariable(Type assigned, TypeVariable<?> assignee) {
        Type[] assigneeUppers = assignee.getBounds();
        for (Type assigneeUpper : assigneeUppers) {
            if (isAssignable(assigned, assigneeUpper)) {
                return true;
            }
        }
        return false;
    }
}
