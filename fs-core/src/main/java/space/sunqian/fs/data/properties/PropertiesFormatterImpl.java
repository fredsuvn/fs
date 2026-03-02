package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

import java.io.Writer;

enum PropertiesFormatterImpl implements PropertiesFormatter {
    INST;

    @Override
    public void formatTo(@Nonnull PropertiesData data, @Nonnull Writer writer) throws IORuntimeException {
        data.writeTo(writer);
    }
}
