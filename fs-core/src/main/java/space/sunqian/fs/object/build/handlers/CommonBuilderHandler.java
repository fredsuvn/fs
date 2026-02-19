package space.sunqian.fs.object.build.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.build.BuilderExecutor;
import space.sunqian.fs.object.build.BuilderProvider;
import space.sunqian.fs.object.build.ObjectBuildingException;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * This is the common implementation of {@link BuilderProvider.Handler}. The {@link BuilderExecutor} returned by this
 * handler using the empty constructor of the target class to create the builder instance, and returns builder instance
 * itself as the final target object.
 * <p>
 * Using {@link #getInstance()} can get a same one instance of this handler.
 *
 * @author sunqian
 */
public class CommonBuilderHandler implements BuilderProvider.Handler {

    private static final @Nonnull CommonBuilderHandler INST = new CommonBuilderHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull CommonBuilderHandler getInstance() {
        return INST;
    }

    @Override
    public @Nullable BuilderExecutor newExecutor(@Nonnull Type target) throws Exception {
        Class<?> rawTarget = TypeKit.getRawClass(target);
        if (rawTarget == null) {
            return null;
        }
        try {
            Constructor<?> cst = rawTarget.getConstructor();
            return new ExecutorImpl(target, cst);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static final class ExecutorImpl implements BuilderExecutor {

        private final @Nonnull Type targetType;
        private final @Nonnull Invocable constructor;

        private ExecutorImpl(
            @Nonnull Type targetType,
            @Nonnull Constructor<?> constructor
        ) {
            this.targetType = targetType;
            this.constructor = Invocable.of(constructor);
        }

        @Override
        public @Nonnull Type builderType() {
            return targetType;
        }

        @Override
        public @Nonnull Type targetType() {
            return targetType;
        }

        @Override
        public @Nonnull Object createBuilder() throws ObjectBuildingException {
            try {
                return constructor.invoke(null);
            } catch (Exception e) {
                throw new ObjectBuildingException(e);
            }
        }

        @Override
        public @Nonnull Object buildTarget(@Nonnull Object builder) throws ObjectBuildingException {
            return builder;
        }
    }
}
