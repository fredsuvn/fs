package xyz.sunqian.common.runtime.invoke;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

final class ByReflection {

    static @Nonnull Invocable newInvocable(@Nonnull Constructor<?> constructor) {
        return new ForConstructor(constructor);
    }

    static @Nonnull Invocable newInvocable(@Nonnull Method method) {
        return new ForMethod(method);
    }

    private static final class ForConstructor implements Invocable {

        private final @Nonnull Constructor<?> constructor;

        private ForConstructor(@Nonnull Constructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        public @Nonnull Object invoke(
            @Nullable Object inst, @Nullable Object @Nonnull ... args
        ) throws InvocationException {
            try {
                return constructor.newInstance(args);
            } catch (Throwable e) {
                throw new InvocationException(e);
            }
        }

        @Override
        public Object invokeChecked(
            @Nullable Object inst, @Nullable Object @Nonnull ... args
        ) throws Throwable {
            try {
                return constructor.newInstance(args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    private static final class ForMethod implements Invocable {

        private final @Nonnull Method method;

        private ForMethod(@Nonnull Method method) {
            this.method = method;
        }

        @Override
        public @Nullable Object invoke(
            @Nullable Object inst, @Nullable Object @Nonnull ... args
        ) throws InvocationException {
            try {
                return method.invoke(inst, args);
            } catch (Throwable e) {
                throw new InvocationException(e);
            }
        }

        @Override
        public @Nullable Object invokeChecked(
            @Nullable Object inst, @Nullable Object @Nonnull ... args
        ) throws Throwable {
            try {
                return method.invoke(inst, args);
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }
}
