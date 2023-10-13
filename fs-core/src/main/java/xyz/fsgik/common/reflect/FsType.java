package xyz.fsgik.common.reflect;

import xyz.fsgik.annotations.Nullable;
import xyz.fsgik.common.base.FsArray;
import xyz.fsgik.common.base.FsString;
import xyz.fsgik.common.collect.FsCollect;

import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;
import java.util.Objects;

/**
 * Utilities for types.
 *
 * @author fredsuvn
 */
public class FsType {

    /**
     * Returns a ParameterizedType with given raw type, owner type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param ownerType           given owner type
     * @param actualTypeArguments actual type arguments
     */
    public static ParameterizedType parameterizedType(
        Type rawType, @Nullable Type ownerType, Iterable<Type> actualTypeArguments) {
        return new FsParameterizedType(rawType, ownerType, FsCollect.toArray(actualTypeArguments, Type.class));
    }

    /**
     * Returns a ParameterizedType with given raw type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param actualTypeArguments actual type arguments
     */
    public static ParameterizedType parameterizedType(Type rawType, Iterable<Type> actualTypeArguments) {
        return parameterizedType(rawType, null, actualTypeArguments);
    }

    /**
     * Returns a ParameterizedType with given raw type, owner type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param ownerType           given owner type
     * @param actualTypeArguments actual type arguments
     */
    public static ParameterizedType parameterizedType(
        Type rawType, @Nullable Type ownerType, Type[] actualTypeArguments) {
        return new FsParameterizedType(rawType, ownerType, actualTypeArguments);
    }

    /**
     * Returns a ParameterizedType with given raw type and actual type arguments.
     *
     * @param rawType             given raw type
     * @param actualTypeArguments actual type arguments
     */
    public static ParameterizedType parameterizedType(Type rawType, Type[] actualTypeArguments) {
        return parameterizedType(rawType, null, actualTypeArguments);
    }

    /**
     * Returns a WildcardType with given upper bounds and lower bounds.
     *
     * @param upperBounds given upper bounds
     * @param lowerBounds given lower bounds
     */
    public static WildcardType wildcardType(
        @Nullable Iterable<Type> upperBounds, @Nullable Iterable<Type> lowerBounds) {
        return new FsWildcardType(
            upperBounds == null ? null : FsCollect.toArray(upperBounds, Type.class),
            lowerBounds == null ? null : FsCollect.toArray(lowerBounds, Type.class)
        );
    }

    /**
     * Returns a WildcardType with given upper bounds and lower bounds.
     *
     * @param upperBounds given upper bounds
     * @param lowerBounds given lower bounds
     */
    public static WildcardType wildcardType(
        @Nullable Type[] upperBounds, @Nullable Type[] lowerBounds) {
        return new FsWildcardType(upperBounds, lowerBounds);
    }

    /**
     * Returns a GenericArrayType with given component type.
     *
     * @param componentType given component type
     */
    public static GenericArrayType genericArrayType(Type componentType) {
        return new FsGenericArrayType(componentType);
    }

    private static final class FsParameterizedType implements ParameterizedType {

        private final Type rawType;
        private final @Nullable Type ownerType;
        private final Type[] actualTypeArguments;

        private FsParameterizedType(Type rawType, @Nullable Type ownerType, Type[] actualTypeArguments) {
            this.rawType = rawType;
            this.ownerType = ownerType == null ?
                ((rawType instanceof Class) ? ((Class<?>) rawType).getDeclaringClass() : null) : ownerType;
            this.actualTypeArguments = actualTypeArguments;
        }

        @Override
        public Type[] getActualTypeArguments() {
            return actualTypeArguments.clone();
        }

        @Override
        public Type getRawType() {
            return rawType;
        }

        @Override
        public Type getOwnerType() {
            return ownerType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof FsParameterizedType) {
                FsParameterizedType other = (FsParameterizedType) o;
                return Objects.equals(rawType, other.rawType)
                    && Objects.equals(ownerType, other.ownerType)
                    && Arrays.equals(actualTypeArguments, other.actualTypeArguments);
            }
            if (o instanceof ParameterizedType) {
                ParameterizedType other = (ParameterizedType) o;
                return Objects.equals(rawType, other.getRawType())
                    && Objects.equals(ownerType, other.getOwnerType())
                    && Arrays.equals(actualTypeArguments, other.getActualTypeArguments());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(this.actualTypeArguments)
                ^ (this.ownerType == null ? 0 : this.ownerType.hashCode())
                ^ (this.rawType == null ? 0 : this.rawType.hashCode());
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (ownerType != null) {
                //test.A<String>
                sb.append(ownerType.getTypeName());
                //test.A$B
                String rawTypeName = rawType.getTypeName();
                //test.A
                String ownerRawTypeName;
                if (ownerType instanceof ParameterizedType) {
                    ownerRawTypeName = ((ParameterizedType) ownerType).getRawType().getTypeName();
                } else {
                    ownerRawTypeName = ownerType.getTypeName();
                }
                //test.A$B -> $B
                String lastName = FsString.replace(rawTypeName, ownerRawTypeName, "");
                //test.A<String>$B
                sb.append(lastName);
            } else {
                sb.append(rawType.getTypeName());
            }
            if (FsArray.isNotEmpty(actualTypeArguments)) {
                sb.append("<");
                boolean first = true;
                for (Type t : actualTypeArguments) {
                    if (!first) {
                        sb.append(", ");
                    }
                    if (t instanceof Class) {
                        sb.append(((Class<?>) t).getName());
                    } else {
                        sb.append(t.toString());
                    }
                    first = false;
                }
                sb.append(">");
            }
            return sb.toString();
        }
    }

    private static final class FsWildcardType implements WildcardType {

        private final Type[] upperBounds;
        private final Type[] lowerBounds;

        private FsWildcardType(@Nullable Type[] upperBounds, @Nullable Type[] lowerBounds) {
            this.upperBounds = FsArray.isEmpty(upperBounds) ? new Type[]{Object.class} : upperBounds;
            this.lowerBounds = FsArray.isEmpty(lowerBounds) ? new Type[0] : lowerBounds;
        }

        @Override
        public Type[] getUpperBounds() {
            return upperBounds.clone();
        }

        @Override
        public Type[] getLowerBounds() {
            return lowerBounds.clone();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof FsWildcardType) {
                FsWildcardType other = (FsWildcardType) o;
                return Arrays.equals(upperBounds, other.upperBounds)
                    && Arrays.equals(lowerBounds, other.lowerBounds);
            }
            if (o instanceof WildcardType) {
                WildcardType other = (WildcardType) o;
                return Arrays.equals(upperBounds, other.getUpperBounds())
                    && Arrays.equals(lowerBounds, other.getLowerBounds());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(upperBounds) ^ Arrays.hashCode(lowerBounds);
        }

        @Override
        public String toString() {
            StringBuilder builder;
            Type[] bounds;
            if (lowerBounds.length == 0) {
                if (upperBounds.length == 0 || Objects.equals(Object.class, upperBounds[0])) {
                    return "?";
                }

                bounds = upperBounds;
                builder = new StringBuilder("? extends ");
            } else {
                bounds = lowerBounds;
                builder = new StringBuilder("? super ");
            }
            for (int i = 0; i < bounds.length; ++i) {
                if (i > 0) {
                    builder.append(" & ");
                }

                builder.append(bounds[i] instanceof Class ? ((Class<?>) bounds[i]).getName() : bounds[i].toString());
            }
            return builder.toString();
        }
    }

    private static final class FsGenericArrayType implements GenericArrayType {

        private final Type componentType;

        private FsGenericArrayType(Type componentType) {
            this.componentType = componentType;
        }

        @Override
        public Type getGenericComponentType() {
            return componentType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o instanceof GenericArrayType) {
                GenericArrayType other = (GenericArrayType) o;
                return Objects.equals(componentType, other.getGenericComponentType());
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(componentType);
        }

        @Override
        public String toString() {
            Type type = this.getGenericComponentType();
            StringBuilder builder = new StringBuilder();
            if (type instanceof Class) {
                builder.append(((Class<?>) type).getName());
            } else {
                builder.append(type.toString());
            }
            builder.append("[]");
            return builder.toString();
        }
    }
}
