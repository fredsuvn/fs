package space.sunqian.fs.object.data.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.data.ObjectSchemaParser;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * This is an implementation of {@link ObjectSchemaParser.Handler} which basically follows the <a
 * href="https://www.oracle.com/java/technologies/javase/javabeans-spec.html">JavaBeans</a> style, inheriting from
 * {@link AbstractObjectSchemaHandler} and overriding the {@link AbstractObjectSchemaHandler#resolveAccessor(Method)}
 * method.
 * <p>
 * This implementation resolves {@code getXxx} or {@code isXxx} methods as getters and {@code setXxx} methods as setters
 * according to lower camel case naming conventions.
 *
 * @author sunqian
 */
public class SimpleBeanSchemaHandler extends AbstractObjectSchemaHandler {

    @Override
    protected @Nullable AccessorInfo resolveAccessor(@Nonnull Method method) {
        int parameterCount = method.getParameterCount();
        switch (parameterCount) {
            case 0:// maybe getter
                return tryGetter(method);
            case 1:// maybe setter
                return trySetter(method);
        }
        return null;
    }

    private @Nullable AccessorInfo tryGetter(@Nonnull Method method) {
        Class<?> returnType = method.getReturnType();
        if (Objects.equals(returnType, void.class)) {
            return null;
        }
        String propertyName = propertyNameFromGetter(method);
        if (propertyName == null) {
            return null;
        }
        return new AccessorInfoImpl(propertyName, Invocable.of(method), true);
    }

    private @Nullable String propertyNameFromGetter(@Nonnull Method method) {
        // getter's name should be getXxx or isXxx
        String methodName = method.getName();
        if (methodName.length() > 3 && methodName.startsWith("get")) {
            if (!Character.isUpperCase(methodName.charAt(3))) {
                return null;
            }
            if (methodName.length() == 4) {
                return String.valueOf(Character.toLowerCase(methodName.charAt(3)));
            }
            if (Character.isUpperCase(methodName.charAt(4))) {
                return methodName.substring(3);
            } else {
                return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            }
        }
        if (methodName.length() > 2 && methodName.startsWith("is")) {
            if (!Character.isUpperCase(methodName.charAt(2))) {
                return null;
            }
            if (methodName.length() == 3) {
                return String.valueOf(Character.toLowerCase(methodName.charAt(2)));
            }
            if (Character.isUpperCase(methodName.charAt(3))) {
                return methodName.substring(2);
            } else {
                return Character.toLowerCase(methodName.charAt(2)) + methodName.substring(3);
            }
        }
        return null;
    }

    private @Nullable AccessorInfo trySetter(@Nonnull Method method) {
        String propertyName = propertyNameFromSetter(method);
        if (propertyName == null) {
            return null;
        }
        return new AccessorInfoImpl(propertyName, Invocable.of(method), false);
    }

    private @Nullable String propertyNameFromSetter(@Nonnull Method method) {
        // setter's name should be setXxx
        String methodName = method.getName();
        if (methodName.length() > 3 && methodName.startsWith("set")) {
            if (!Character.isUpperCase(methodName.charAt(3))) {
                return null;
            }
            if (methodName.length() == 4) {
                return String.valueOf(Character.toLowerCase(methodName.charAt(3)));
            }
            if (Character.isUpperCase(methodName.charAt(4))) {
                return methodName.substring(3);
            } else {
                return Character.toLowerCase(methodName.charAt(3)) + methodName.substring(4);
            }
        }
        return null;
    }

    private static final class AccessorInfoImpl implements AccessorInfo {

        private final @Nonnull String name;
        private final @Nonnull Invocable accessor;
        private final boolean isGetter;

        private AccessorInfoImpl(@Nonnull String name, @Nonnull Invocable accessor, boolean isGetter) {
            this.name = name;
            this.accessor = accessor;
            this.isGetter = isGetter;
        }

        @Override
        public @Nonnull String propertyName() {
            return name;
        }

        @Override
        public @Nonnull Invocable accessor() {
            return accessor;
        }

        @Override
        public boolean isGetter() {
            return isGetter;
        }
    }
}
