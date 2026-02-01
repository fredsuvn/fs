package space.sunqian.fs.object.convert;

import space.sunqian.annotation.Nonnull;

import java.lang.reflect.Type;

enum PropertyNameMapperImpl implements PropertyNameMapper {
    INST;

    @Override
    public @Nonnull String map(@Nonnull String srcPropertyName, @Nonnull Type srcType) {
        return srcPropertyName;
    }
}
