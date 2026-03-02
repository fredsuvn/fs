package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.fs.base.date.DateFormatter;
import space.sunqian.fs.base.number.NumFormatter;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.option.OptionKit;
import space.sunqian.fs.object.annotation.DatePattern;
import space.sunqian.fs.object.annotation.NumPattern;
import space.sunqian.fs.object.schema.ObjectProperty;

import java.lang.annotation.Annotation;

final class ConvertBack {

    static @Nonnull Option<?, ?> @Nonnull [] mergeOptions(
        @Nonnull Option<?, ?> @Nonnull @RetainedParam [] defaultOptions,
        @Nullable DatePattern datePattern,
        @Nullable NumPattern numPattern
    ) {
        if (datePattern == null) {
            if (numPattern == null) {
                return defaultOptions;
            } else {
                Option<ConvertOption, NumFormatter> numFormatter = ConvertKit.getNumFormatterOption(numPattern);
                return OptionKit.mergeOption(defaultOptions, numFormatter);
            }
        } else {
            Option<ConvertOption, DateFormatter> dateFormatter = ConvertKit.getDateFormatterOption(datePattern);
            if (numPattern == null) {
                return OptionKit.mergeOption(defaultOptions, dateFormatter);
            } else {
                Option<ConvertOption, NumFormatter> numFormatter = ConvertKit.getNumFormatterOption(numPattern);
                return OptionKit.mergeOptions(defaultOptions, dateFormatter, numFormatter);
            }
        }
    }

    static <A extends Annotation> @Nullable A getAnnotation(
        @Nonnull Class<A> annotationType,
        @Nonnull ObjectProperty srcProperty,
        @Nonnull ObjectProperty dstProperty
    ) {
        A srcAnnotation = srcProperty.getAnnotation(annotationType);
        A dstAnnotation = dstProperty.getAnnotation(annotationType);
        if (dstAnnotation == null) {
            return srcAnnotation;
        } else {
            return dstAnnotation;
        }
    }

    private ConvertBack() {
    }
}
