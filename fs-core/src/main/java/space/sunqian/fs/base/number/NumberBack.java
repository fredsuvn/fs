package space.sunqian.fs.base.number;

import space.sunqian.annotation.Nonnull;

import java.text.NumberFormat;
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

    private NumberBack() {
    }
}