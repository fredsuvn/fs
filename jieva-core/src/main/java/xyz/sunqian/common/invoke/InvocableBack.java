package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class InvocableBack {

    static Invocable ofConstructor(Constructor<?> constructor) {
        return new OfConstructor(constructor);
    }

    static Invocable ofMethod(Method method) {
        return new OfMethod(method);
    }

    static Invocable ofMethodHandle(Constructor<?> constructor) {
        return new OfMethodHandle(constructor);
    }

    static Invocable ofMethodHandle(Method method) {
        return new OfMethodHandle(method);
    }

    static Invocable ofMethodHandle(MethodHandle methodHandle, boolean isStatic) {
        return new OfMethodHandle(methodHandle, isStatic);
    }

    private static InvocationException buildInvocationException(Throwable rawCause) {
        if (rawCause instanceof InvocationTargetException) {
            return new InvocationException(rawCause.getCause());
        }
        return new InvocationException(rawCause);
    }

    private static final class OfConstructor implements Invocable {

        private final Constructor<?> constructor;

        private OfConstructor(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, Object... args) {
            try {
                return constructor.newInstance(args);
            } catch (Exception e) {
                throw buildInvocationException(e);
            }
        }
    }

    private static final class OfMethod implements Invocable {

        private final Method method;

        private OfMethod(Method method) {
            this.method = method;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, Object... args) {
            try {
                return method.invoke(inst, args);
            } catch (Exception e) {
                throw buildInvocationException(e);
            }
        }
    }

    private static final class OfMethodHandle implements Invocable {

        private final MethodHandle methodHandle;
        private final boolean isStatic;

        private OfMethodHandle(Method method) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflect(method);
                this.isStatic = Modifier.isStatic(method.getModifiers());
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        private OfMethodHandle(Constructor<?> constructor) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
                this.isStatic = true;
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        private OfMethodHandle(MethodHandle methodHandle, boolean isStatic) {
            this.methodHandle = methodHandle;
            this.isStatic = isStatic;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, Object... args) {
            try {
                return isStatic ? JieHandle.invokeStatic(methodHandle, args)
                    : JieHandle.invokeInstance(methodHandle, inst, args);
            } catch (Throwable e) {
                throw new InvocationException(e);
            }
        }
    }
}
