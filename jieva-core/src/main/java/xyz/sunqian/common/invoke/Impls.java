package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class Impls {

    static Invoker ofConstructor(Constructor<?> constructor) {
        return new OfConstructor(constructor);
    }

    static Invoker ofMethod(Method method) {
        return new OfMethod(method);
    }

    static Invoker ofMethodHandle(Constructor<?> constructor) {
        return new OfMethodHandle(constructor);
    }

    static Invoker ofMethodHandle(Method method) {
        return new OfMethodHandle(method);
    }

    static Invoker ofMethodHandle(MethodHandle methodHandle, boolean isStatic) {
        return new OfMethodHandle(methodHandle, isStatic);
    }

    private static final class OfConstructor implements Invoker {

        private final Constructor<?> constructor;

        OfConstructor(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, Object... args) {
            try {
                return constructor.newInstance(args);
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    throw new InvocationException(e.getCause());
                }
                throw new InvocationException(e);
            }
        }
    }

    private static final class OfMethod implements Invoker {

        private final Method method;

        OfMethod(Method method) {
            this.method = method;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, Object... args) {
            try {
                return method.invoke(inst, args);
            } catch (Exception e) {
                if (e instanceof InvocationTargetException) {
                    throw new InvocationException(e.getCause());
                }
                throw new InvocationException(e);
            }
        }
    }

    private static final class OfMethodHandle implements Invoker {

        private final MethodHandle methodHandle;
        private final boolean isStatic;

        OfMethodHandle(Method method) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflect(method);
                this.isStatic = Modifier.isStatic(method.getModifiers());
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        OfMethodHandle(Constructor<?> constructor) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
                this.isStatic = true;
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        OfMethodHandle(MethodHandle methodHandle, boolean isStatic) {
            this.methodHandle = methodHandle;
            this.isStatic = isStatic;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, Object... args) {
            try {
                return isStatic ? JieInvoke.invokeStatic(methodHandle, args)
                    : JieInvoke.invokeInstance(methodHandle, inst, args);
            } catch (Throwable e) {
                throw new InvocationException(e);
            }
        }
    }
}
