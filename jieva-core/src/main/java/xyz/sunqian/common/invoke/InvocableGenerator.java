package xyz.sunqian.common.invoke;

import xyz.sunqian.annotations.Nonnull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

interface InvocableGenerator {

    @Nonnull
    Invocable generate(@Nonnull Method method);

    @Nonnull
    Invocable generate(@Nonnull Constructor<?> constructor);
}
