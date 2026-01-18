package space.sunqian.fs.object.create.handlers;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.invoke.Invocable;
import space.sunqian.fs.object.create.CreatorProvider;
import space.sunqian.fs.object.create.ObjectCreateException;
import space.sunqian.fs.object.create.ObjectCreator;
import space.sunqian.fs.reflect.TypeKit;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * This is the common implementation of {@link CreatorProvider.Handler}. The {@link ObjectCreator} returned by this
 * handler using the empty constructor of the target class to create the builder instance, and returns builder instance
 * itself as the final target object.
 * <p>
 * An instance {@link #INSTANCE} is provided for convenience and less memory usage.
 *
 * @author sunqian
 */
public class CommonCreatorHandler implements CreatorProvider.Handler {

    /**
     * An instance of this handler.
     */
    public static final @Nonnull CommonCreatorHandler INSTANCE = new CommonCreatorHandler();

    @Override
    public @Nullable ObjectCreator newCreator(@Nonnull Type target) throws Exception {
        Class<?> rawTarget = TypeKit.getRawClass(target);
        if (rawTarget == null) {
            return null;
        }
        try {
            Constructor<?> cst = rawTarget.getConstructor();
            return new CreatorImpl(target, cst);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private static final class CreatorImpl implements ObjectCreator {

        private final @Nonnull Type targetType;
        private final @Nonnull Invocable constructor;

        private CreatorImpl(
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
        public @Nonnull Object createBuilder() throws ObjectCreateException {
            try {
                return constructor.invoke(null);
            } catch (Exception e) {
                throw new ObjectCreateException(e);
            }
        }

        @Override
        public @Nonnull Object buildTarget(@Nonnull Object builder) throws ObjectCreateException {
            return builder;
        }
    }
}
