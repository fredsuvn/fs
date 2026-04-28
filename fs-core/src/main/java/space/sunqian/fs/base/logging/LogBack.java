package space.sunqian.fs.base.logging;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.fs.Fs;

import java.util.function.Supplier;

final class LogBack {

    static <T> @Nonnull ToLog<T> toLog(@Nullable T origin) {
        return new ToLogOfObject<>(origin);
    }

    static @Nonnull ToLog<@Nonnull Supplier<@Nonnull String>> toLog(@Nonnull Supplier<@Nonnull String> supplier) {
        return new ToLogOfSupplier(supplier);
    }

    private static final class ToLogOfObject<T> implements ToLog<T> {

        private final @Nullable T origin;
        private @Nullable String value;

        private ToLogOfObject(@Nullable T origin) {
            this.origin = origin;
        }

        @Override
        public T origin() {
            return origin;
        }

        @Override
        public @Nonnull String toString() {
            if (value == null) {
                value = Fs.toString(origin);
            }
            return value;
        }
    }

    private static final class ToLogOfSupplier implements ToLog<Supplier<String>> {

        private final @Nonnull Supplier<@Nonnull String> supplier;
        private @Nullable String value;

        private ToLogOfSupplier(@Nonnull Supplier<@Nonnull String> supplier) {
            this.supplier = supplier;
        }

        @Override
        public @Nonnull Supplier<@Nonnull String> origin() {
            return supplier;
        }

        @Override
        public @Nonnull String toString() {
            if (value == null) {
                value = supplier.get();
            }
            return value;
        }
    }

    private LogBack() {
    }
}
