package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

final class InvocableBack {

    static Invocable withReflection(Constructor<?> constructor) {
        return new ConstructorReflection(constructor);
    }

    static Invocable withReflection(Method method) {
        return new MethodReflection(method);
    }

    static Invocable withMethodHandle(Constructor<?> constructor) {
        return new StaticMethodHandle(constructor);
    }

    static Invocable withMethodHandle(Method method) {
        return Modifier.isStatic(method.getModifiers()) ?
            new StaticMethodHandle(method) : new InstanceMethodHandle(method);
    }

    static Invocable ofMethodHandle(MethodHandle methodHandle, boolean isStatic) {
        return isStatic ?
            new StaticMethodHandle(methodHandle) : new InstanceMethodHandle(methodHandle);
    }

    private static InvocationException buildInvocationException(Throwable original) {
        if (original instanceof InvocationTargetException) {
            return new InvocationException(original.getCause());
        }
        return new InvocationException(original);
    }

    private static final class ConstructorReflection implements Invocable {

        private final Constructor<?> constructor;

        private ConstructorReflection(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        public Object invoke(@Nullable Object inst, @Nullable Object... args) {
            try {
                return constructor.newInstance(args);
            } catch (Exception e) {
                throw buildInvocationException(e);
            }
        }
    }

    private static final class MethodReflection implements Invocable {

        private final Method method;

        private MethodReflection(Method method) {
            this.method = method;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, @Nullable Object... args) {
            try {
                return method.invoke(inst, args);
            } catch (Exception e) {
                throw buildInvocationException(e);
            }
        }
    }

    private static final class InstanceMethodHandle implements Invocable {

        private final MethodHandle methodHandle;

        private InstanceMethodHandle(Method method) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflect(method);
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        private InstanceMethodHandle(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, @Nullable Object... args) {
            if (inst == null) {
                throw new InvocationException("The instance must be nonnull.");
            }
            try {
                return JieHandle.invokeInstance(methodHandle, inst, args);
            } catch (Throwable e) {
                throw new InvocationException(e);
            }
        }
    }

    private static final class StaticMethodHandle implements Invocable {

        private final MethodHandle methodHandle;

        private StaticMethodHandle(Method method) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflect(method);
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        private StaticMethodHandle(Constructor<?> constructor) {
            try {
                this.methodHandle = MethodHandles.lookup().unreflectConstructor(constructor);
            } catch (Exception e) {
                throw new InvocationException(e);
            }
        }

        private StaticMethodHandle(MethodHandle methodHandle) {
            this.methodHandle = methodHandle;
        }

        @Override
        public @Nullable Object invoke(@Nullable Object inst, @Nullable Object... args) {
            try {
                return JieHandle.invokeStatic(methodHandle, args);
            } catch (Throwable e) {
                throw new InvocationException(e);
            }
        }
    }
}
