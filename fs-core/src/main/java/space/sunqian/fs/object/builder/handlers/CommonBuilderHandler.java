package space.sunqian.fs.object.builder.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.builder.BuilderOperator;
import space.sunqian.fs.object.builder.BuilderOperatorProvider;
import space.sunqian.fs.object.builder.ObjectBuilderException;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * This is the common implementation of {@link BuilderOperatorProvider.Handler}. The {@link BuilderOperator} returned by
 * this handler using the empty constructor of the target class to create the builder instance, and returns builder
 * instance itself as the final target object.
 * <p>
 * Using {@link #getInstance()} can get a same one instance of this handler.
 *
 * @author sunqian
 */
public class CommonBuilderHandler implements BuilderOperatorProvider.Handler {

    private static final @Nonnull CommonBuilderHandler INST = new CommonBuilderHandler();

    /**
     * Returns a same one instance of this handler.
     */
    public static @Nonnull CommonBuilderHandler getInstance() {
        return INST;
    }

    @Override
    public @Nullable BuilderOperator newOperator(@Nonnull Type target) throws Exception {
        Class<?> rawTarget = TypeKit.getRawClass(target);
        if (rawTarget == null) {
            return null;
        }
        try {
            Constructor<?> cst = rawTarget.getConstructor();
            return new CommonBuilderOperator(target, cst);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static final class CommonBuilderOperator implements BuilderOperator {

        private final @Nonnull Type targetType;
        private final @Nonnull Invocable constructor;

        private CommonBuilderOperator(
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
        public @Nonnull Object createBuilder() throws ObjectBuilderException {
            try {
                return constructor.invoke(null);
            } catch (Exception e) {
                throw new ObjectBuilderException(e);
            }
        }

        @Override
        public @Nonnull Object buildTarget(@Nonnull Object builder) throws ObjectBuilderException {
            return builder;
        }
    }
}
