package xyz.sunqian.common.object.data;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.collect.ListKit;
import xyz.sunqian.common.object.data.handlers.AbstractDataSchemaHandler;
import xyz.sunqian.common.object.data.handlers.JavaBeanDataSchemaHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to parse {@link Type} to {@link DataSchema}. It uses a list of {@link Handler}s to execute the
 * specific parsing operations, where each {@link Handler} possesses its own specific parsing logic.
 * <p>
 * There is a skeletal handler implementation: {@link AbstractDataSchemaHandler}. And the default parser is based on
 * {@link JavaBeanDataSchemaHandler}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface DataSchemaParser {

    /**
     * Returns the default {@link DataSchemaParser}, of which handler is {@link JavaBeanDataSchemaHandler}.
     *
     * @return the default {@link DataSchemaParser}
     */
    static @Nonnull DataSchemaParser defaultParser() {
        return DataSchemaParserImpl.SINGLETON;
    }

    /**
     * Creates and returns a new {@link DataSchemaParser} with the given handlers.
     *
     * @param handlers the given handlers
     * @return a new {@link DataSchemaParser} with the given handlers
     */
    static @Nonnull DataSchemaParser withHandlers(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return withHandlers(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link DataSchemaParser} with given handlers.
     *
     * @param handlers given handlers
     * @return a new {@link DataSchemaParser} with given handlers
     */
    static @Nonnull DataSchemaParser withHandlers(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return new DataSchemaParserImpl(handlers);
    }

    /**
     * Parses the given type to a {@link DataSchema}, and returns the parsed {@link DataSchema}.
     * <p>
     * The parsing logic of the implementations must be: invokes the {@link Handler#parse(DataSchemaParser.Context)} in
     * the order of {@link #handlers()} until one of the handlers returns {@code false}. The code is similar to:
     * <pre>{@code
     * for (Handler handler : handlers()) {
     *     if (!handler.parse(context)) {
     *         break;
     *     }
     * }
     * }</pre>
     * <p>
     * Note that this method does not cache the results and will generate new instances every invocation.
     *
     * @param type the given type
     * @return the parsed {@link DataSchema}
     * @throws DataObjectException if any problem occurs
     */
    default @Nonnull DataSchema parse(Type type) throws DataObjectException {
        try {
            DataSchemaBuilder builder = new DataSchemaBuilder(type);
            for (Handler handler : handlers()) {
                if (!handler.parse(builder)) {
                    break;
                }
            }
            return builder.build(this);
        } catch (Exception e) {
            throw new DataObjectException(type, e);
        }
    }

    /**
     * Returns all handlers of this parser.
     *
     * @return all handlers of this parser
     */
    @Nonnull
    @Immutable
    List<Handler> handlers();

    /**
     * Returns a new {@link DataSchemaParser} of which handler list consists of the given handler as the first element,
     * followed by {@link #handlers()} of the current parser.
     *
     * @param handler the given handler
     * @return a new {@link DataSchemaParser} of which handler list consists of the given handler as the first element,
     * followed by {@link #handlers()} of the current parser
     */
    default @Nonnull DataSchemaParser withFirstHandler(Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        newHandlers[i++] = handler;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        return withHandlers(newHandlers);
    }

    /**
     * Returns a new {@link DataSchemaParser} of which handler list consists of {@link #handlers()} of the current
     * parser, followed by the given handler as the last element.
     *
     * @param handler the given handler
     * @return a {@link DataSchemaParser} of which handler list consists of {@link #handlers()} of the current parser,
     * followed by the given handler as the last element
     */
    default @Nonnull DataSchemaParser withLastHandler(Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        newHandlers[i] = handler;
        return withHandlers(newHandlers);
    }

    /**
     * Returns this parser as a {@link Handler}.
     *
     * @return this parser as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link DataSchemaParser}, provides the specific parsing logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Parses {@link Type} to {@link DataSchema} with its owner parsing logic. The {@link Type} is specified by
         * {@link Context#dataType()} of the given context, and the parsed properties should be stored in
         * {@link Context#propertyBaseMap()}. Returns {@code false} to prevent subsequent handlers to continue to parse,
         * otherwise returns {@code true}.
         *
         * @param context the given context
         * @return whether to continue to parse
         * @throws Exception for errors during parsing
         */
        boolean parse(@Nonnull Context context) throws Exception;
    }

    /**
     * Context for parsing the specified {@link Type} to {@link DataSchema}.
     *
     * @author sunqian
     */
    interface Context {

        /**
         * Returns the specified type.
         *
         * @return the specified type
         */
        @Nonnull
        Type dataType();

        /**
         * Returns a mutable map for storing property base infos.
         * <p>
         * The map through whole parsing process, stores and shares the property base infos for all handlers, and each
         * handler can add or remove or modify the property base info.
         *
         * @return a mutable map for storing property base infos
         */
        @Nonnull
        Map<@Nonnull String, @Nonnull DataPropertyBase> propertyBaseMap();
    }
}
