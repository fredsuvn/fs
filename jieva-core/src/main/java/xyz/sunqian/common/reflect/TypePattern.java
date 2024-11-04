package xyz.sunqian.common.reflect;

import java.lang.reflect.*;
import java.util.Objects;

interface TypePattern {

    static TypePattern defaultPattern() {
        return TypePatternImpl.INSTANCE;
    }

    default boolean isAssignable(Type assigned, Type assignee) {
        if (assigned instanceof Class<?>) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return isAssignableClassToClass((Class<?>) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableClassToParameterized((Class<?>) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableClassToWildcard((Class<?>) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableClassToTypeVariable((Class<?>) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableClassToGenericArray((Class<?>) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        if (assigned instanceof ParameterizedType) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return isAssignableParameterizedToClass((ParameterizedType) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableParameterizedToParameterized((ParameterizedType) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableParameterizedToWildcard((ParameterizedType) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableParameterizedToTypeVariable((ParameterizedType) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableParameterizedToGenericArray((ParameterizedType) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        if (assigned instanceof WildcardType) {
            if (assignee instanceof Class<?>) {
                return isAssignableWildcardToClass((WildcardType) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableWildcardToParameterized((WildcardType) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableWildcardToWildcard((WildcardType) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableWildcardToTypeVariable((WildcardType) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableWildcardToGenericArray((WildcardType) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        if (assigned instanceof TypeVariable<?>) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return isAssignableTypeVariableToClass((TypeVariable<?>) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableTypeVariableToParameterized((TypeVariable<?>) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableTypeVariableToWildcard((TypeVariable<?>) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableTypeVariableToTypeVariable((TypeVariable<?>) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableTypeVariableToGenericArray((TypeVariable<?>) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        if (assigned instanceof GenericArrayType) {
            if (Objects.equals(assigned, assignee)) {
                return true;
            }
            if (assignee instanceof Class<?>) {
                return isAssignableGenericArrayToClass((GenericArrayType) assigned, (Class<?>) assignee);
            }
            if (assignee instanceof ParameterizedType) {
                return isAssignableGenericArrayToParameterized((GenericArrayType) assigned, (ParameterizedType) assignee);
            }
            if (assignee instanceof WildcardType) {
                return isAssignableGenericArrayToWildcard((GenericArrayType) assigned, (WildcardType) assignee);
            }
            if (assignee instanceof TypeVariable<?>) {
                return isAssignableGenericArrayToTypeVariable((GenericArrayType) assigned, (TypeVariable<?>) assignee);
            }
            if (assignee instanceof GenericArrayType) {
                return isAssignableGenericArrayToGenericArray((GenericArrayType) assigned, (GenericArrayType) assignee);
            }
            return false;
        }
        return false;
    }

    boolean isAssignableClassToClass(Class<?> assigned, Class<?> assignee);

    boolean isAssignableClassToParameterized(Class<?> assigned, ParameterizedType assignee);

    boolean isAssignableClassToWildcard(Class<?> assigned, WildcardType assignee);

    boolean isAssignableClassToTypeVariable(Class<?> assigned, TypeVariable<?> assignee);

    boolean isAssignableClassToGenericArray(Class<?> assigned, GenericArrayType assignee);

    boolean isAssignableParameterizedToClass(ParameterizedType assigned, Class<?> assignee);

    boolean isAssignableParameterizedToParameterized(ParameterizedType assigned, ParameterizedType assignee);

    boolean isAssignableParameterizedToWildcard(ParameterizedType assigned, WildcardType assignee);

    boolean isAssignableParameterizedToTypeVariable(ParameterizedType assigned, TypeVariable<?> assignee);

    boolean isAssignableParameterizedToGenericArray(ParameterizedType assigned, GenericArrayType assignee);

    boolean isAssignableWildcardToClass(WildcardType assigned, Class<?> assignee);

    boolean isAssignableWildcardToParameterized(WildcardType assigned, ParameterizedType assignee);

    boolean isAssignableWildcardToWildcard(WildcardType assigned, WildcardType assignee);

    boolean isAssignableWildcardToTypeVariable(WildcardType assigned, TypeVariable<?> assignee);

    boolean isAssignableWildcardToGenericArray(WildcardType assigned, GenericArrayType assignee);

    boolean isAssignableTypeVariableToClass(TypeVariable<?> assigned, Class<?> assignee);

    boolean isAssignableTypeVariableToParameterized(TypeVariable<?> assigned, ParameterizedType assignee);

    boolean isAssignableTypeVariableToWildcard(TypeVariable<?> assigned, WildcardType assignee);

    boolean isAssignableTypeVariableToTypeVariable(TypeVariable<?> assigned, TypeVariable<?> assignee);

    boolean isAssignableTypeVariableToGenericArray(TypeVariable<?> assigned, GenericArrayType assignee);

    boolean isAssignableGenericArrayToClass(GenericArrayType assigned, Class<?> assignee);

    boolean isAssignableGenericArrayToParameterized(GenericArrayType assigned, ParameterizedType assignee);

    boolean isAssignableGenericArrayToWildcard(GenericArrayType assigned, WildcardType assignee);

    boolean isAssignableGenericArrayToTypeVariable(GenericArrayType assigned, TypeVariable<?> assignee);

    boolean isAssignableGenericArrayToGenericArray(GenericArrayType assigned, GenericArrayType assignee);
}
