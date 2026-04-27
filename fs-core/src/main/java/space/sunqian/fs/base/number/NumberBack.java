package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.Fs;
import space.sunqian.fs.cache.SimpleCache;

import java.text.NumberFormat;
import java.util.function.Function;
import java.util.function.Supplier;

final class NumberBack {

    static @Nonnull NumberFormatter newFormatter(@Nonnull Supplier<? extends @Nonnull NumberFormat> supplier) {
        return new NumberFormatterImpl(supplier);
    }

    static final class NumberFormatterImpl implements NumberFormatter {

        private final @Nonnull Supplier<? extends @Nonnull NumberFormat> format;

        NumberFormatterImpl(@Nonnull Supplier<? extends @Nonnull NumberFormat> format) {
            this.format = format;
        }

        @Override
        public @Nonnull String format(@Nonnull Number num) throws NumberException {
            try {
                return format.get().format(num);
            } catch (Exception e) {
                throw new NumberException(e);
            }
        }

        @Override
        public <T> @Nonnull T parse(@Nonnull CharSequence numStr, @Nonnull Class<T> numType) throws NumberException {
            try {
                Number num = format.get().parse(numStr.toString());
                return NumberKit.toNumber(num, numType);
            } catch (Exception e) {
                throw new NumberException(e);
            }
        }
    }

    enum Common implements NumberFormatter {
        INST;

        @Override
        public @Nonnull String format(@Nonnull Number num) throws NumberException {
            return num.toString();
        }

        @Override
        public <T> @Nonnull T parse(@Nonnull CharSequence numStr, @Nonnull Class<T> numType) throws NumberException {
            return NumberKit.toNumber(numStr, numType);
        }
    }

    static final class Cache {

        private static final @Nonnull SimpleCache<
            @Nonnull String,
            @Nonnull NumberFormatter
            > CACHE = SimpleCache.ofSoft();

        static {
            Fs.registerGlobalCache(CACHE);
        }

        static @Nonnull NumberFormatter get(
            @Nonnull String pattern,
            @Nonnull Function<@Nonnull String, @Nonnull NumberFormatter> function
        ) {
            return CACHE.get(pattern, function);
        }

        private Cache() {
        }
    }

    private NumberBack() {
    }
}