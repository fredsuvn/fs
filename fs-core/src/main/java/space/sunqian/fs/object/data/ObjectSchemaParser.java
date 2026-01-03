package space.sunqian.fs.object.data;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.data.handlers.AbstractObjectSchemaHandler;
import space.sunqian.fs.object.data.handlers.SimpleBeanSchemaHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to parse {@link Type} to {@link ObjectSchema}. It uses a list of {@link Handler}s to execute
 * the specific parsing operations, where each {@link Handler} possesses its own specific parsing logic.
 * <p>
 * There is a skeletal handler implementation: {@link AbstractObjectSchemaHandler}. And the default parser is based on
 * {@link SimpleBeanSchemaHandler}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface ObjectSchemaParser {

    /**
     * Returns the default {@link ObjectSchemaParser} with {@link SimpleBeanSchemaHandler} as the only handler.
     *
     * @return the default {@link ObjectSchemaParser}
     */
    static @Nonnull ObjectSchemaParser defaultParser() {
        return ObjectSchemaParserImpl.DEFAULT;
    }

    /**
     * Creates and returns a new {@link ObjectSchemaParser} with the given handlers.
     *
     * @param handlers the given handlers
     * @return a new {@link ObjectSchemaParser} with the given handlers
     */
    static @Nonnull ObjectSchemaParser newParser(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return newParser(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectSchemaParser} with given handlers.
     *
     * @param handlers given handlers
     * @return a new {@link ObjectSchemaParser} with given handlers
     */
    static @Nonnull ObjectSchemaParser newParser(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return new ObjectSchemaParserImpl(handlers);
    }

    /**
     * Parses the given type to an instance of {@link ObjectSchema}, and returns the parsed {@link ObjectSchema}.
     * <p>
     * The parsing logic of the implementations must be: invokes the {@link Handler#parse(ObjectSchemaParser.Context)}
     * in the order of {@link #handlers()} until one of the handlers returns {@code false}. The codes are similar to:
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
     * @return the parsed {@link ObjectSchema}
     * @throws DataObjectException if any problem occurs
     */
    default @Nonnull ObjectSchema parse(@Nonnull Type type) throws DataObjectException {
        try {
            ObjectSchemaBuilder builder = new ObjectSchemaBuilder(type);
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
    List<@Nonnull Handler> handlers();

    /**
     * Returns a new {@link ObjectSchemaParser} of which handler list consists of the given handler as the first
     * element, followed by {@link #handlers()} of the current parser.
     *
     * @param handler the given handler
     * @return a new {@link ObjectSchemaParser} of which handler list consists of the given handler as the first
     * element, followed by {@link #handlers()} of the current parser
     */
    default @Nonnull ObjectSchemaParser withFirstHandler(@Nonnull Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        newHandlers[i++] = handler;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        return newParser(newHandlers);
    }

    /**
     * Returns a new {@link ObjectSchemaParser} of which handler list consists of {@link #handlers()} of the current
     * parser, followed by the given handler as the last element.
     *
     * @param handler the given handler
     * @return a {@link ObjectSchemaParser} of which handler list consists of {@link #handlers()} of the current parser,
     * followed by the given handler as the last element
     */
    default @Nonnull ObjectSchemaParser withLastHandler(@Nonnull Handler handler) {
        Handler[] newHandlers = new Handler[handlers().size() + 1];
        int i = 0;
        for (Handler h : handlers()) {
            newHandlers[i++] = h;
        }
        newHandlers[i] = handler;
        return newParser(newHandlers);
    }

    /**
     * Returns this parser as a {@link Handler}.
     *
     * @return this parser as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link ObjectSchemaParser}, provides the specific parsing logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Parses {@link Type} to {@link ObjectSchema} with its owner parsing logic. The {@link Type} is specified by
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
     * Context for parsing the specified {@link Type} to {@link ObjectSchema}.
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
        Map<@Nonnull String, @Nonnull ObjectPropertyBase> propertyBaseMap();
    }
}
