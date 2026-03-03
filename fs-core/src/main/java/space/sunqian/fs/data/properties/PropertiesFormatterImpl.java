package space.sunqian.fs.data.properties;

import space.sunqian.annotation.Nonnull;
import space.sunqian.fs.io.IORuntimeException;

enum PropertiesFormatterImpl implements PropertiesFormatter {
    INST;

    @Override
    public void formatTo(@Nonnull PropertiesData data, @Nonnull Appendable writer) throws IORuntimeException {
        data.writeTo(writer);
    }
}
