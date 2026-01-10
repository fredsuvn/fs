package space.sunqian.fs.jdbc;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.base.option.Option;
import space.sunqian.fs.base.string.NameFormatter;
import space.sunqian.fs.object.convert.ObjectConverter;

import java.lang.reflect.Type;

public class JdbcConverterImpl implements JdbcConverter {

    private final @Nonnull ObjectConverter objectConverter;
    private final @Nonnull NameFormatter nameFormatter;
    private final @Nonnull Option<?, ?> @Nonnull [] options;

    JdbcConverterImpl(
        @Nonnull ObjectConverter objectConverter,
        @Nonnull NameFormatter nameFormatter,
        @Nonnull Option<?, ?> @Nonnull ... options
    ) {
        this.objectConverter = objectConverter;
        this.nameFormatter = nameFormatter;
        this.options = options;
    }

    @Override
    public <T> @Nonnull T convert(@Nonnull Object jdbcObject, @Nonnull Type javaType) {
        Object javaObject = objectConverter.convert(jdbcObject, javaType, options);
        return null;
    }
}
