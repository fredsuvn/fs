package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.common.object.data.handlers.JavaBeanDataSchemaHandler;

import java.util.Collections;
import java.util.List;

final class DataSchemaParserImpl implements DataSchemaParser, DataSchemaParser.Handler {

    static DataSchemaParserImpl SINGLETON =
        new DataSchemaParserImpl(Collections.singletonList(new JavaBeanDataSchemaHandler()));

    private final List<DataSchemaParser.Handler> handlers;

    DataSchemaParserImpl(@Immutable List<DataSchemaParser.Handler> handlers) {
        this.handlers = handlers;
    }

    @Override
    public @Nonnull List<Handler> handlers() {
        return handlers;
    }

    @Override
    public @Nonnull Handler asHandler() {
        return this;
    }

    @Override
    public boolean parse(@Nonnull Context context) throws Exception {
        for (Handler handler : handlers) {
            if (!handler.parse(context)) {
                return false;
            }
        }
        return true;
    }
}
