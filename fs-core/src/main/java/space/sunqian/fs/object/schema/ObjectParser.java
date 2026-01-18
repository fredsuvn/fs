package space.sunqian.fs.object.schema;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.schema.handlers.AbstractObjectSchemaHandler;
import space.sunqian.fs.object.schema.handlers.SimpleBeanSchemaHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to parse {@link Type} to {@link ObjectSchema}. It uses a list of {@link Handler}s to execute
 * the specific parsing operations, where each {@link Handler} possesses its own specific parsing logic.
 * <p>
 * There is a skeletal handler implementation: {@link AbstractObjectSchemaHandler}. And the default parser is based on
 * {@link SimpleBeanSchemaHandler#INSTANCE}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface ObjectParser {

    /**
     * Returns the default {@link ObjectParser} with {@link SimpleBeanSchemaHandler#INSTANCE} as the only handler.
     * <p>
     * Note the default {@link ObjectParser} is singleton, and never caches the parsed results.
     *
     * @return the default {@link ObjectParser}
     */
    static @Nonnull ObjectParser defaultParser() {
        return SchemaBack.ObjectParserImpl.DEFAULT;
    }

    /**
     * Creates and returns a new {@link ObjectParser} with the given handlers.
     * <p>
     * Note the created {@link ObjectParser} never caches the parsed results.
     *
     * @param handlers the given handlers
     * @return a new {@link ObjectParser} with the given handlers
     */
    static @Nonnull ObjectParser newParser(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return newParser(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectParser} with given handlers.
     * <p>
     * Note the created {@link ObjectParser} never caches the parsed results.
     *
     * @param handlers given handlers
     * @return a new {@link ObjectParser} with given handlers
     */
    static @Nonnull ObjectParser newParser(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return new SchemaBack.ObjectParserImpl(handlers);
    }

    /**
     * Parses the given type to an instance of {@link ObjectSchema}, and returns the parsed {@link ObjectSchema}.
     * <p>
     * The parsing logic of the implementations must be: invokes the {@link Handler#parse(ObjectParser.Context)} in the
     * order of {@link #handlers()} until one of the handlers returns {@code false}. The codes are similar to:
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
     * @throws DataSchemaException if any problem occurs
     */
    default @Nonnull ObjectSchema parse(@Nonnull Type type) throws DataSchemaException {
        try {
            SchemaBack.ObjectSchemaBuilder builder = new SchemaBack.ObjectSchemaBuilder(type);
            for (Handler handler : handlers()) {
                if (!handler.parse(builder)) {
                    break;
                }
            }
            return builder.build(this);
        } catch (Exception e) {
            throw new DataSchemaException(type, e);
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
     * Returns a new parser of which first handler is the given handler and the next handler is this parser as a
     * {@link Handler}. This method is equivalent:
     * <pre>{@code
     * newParser(firstHandler, this.asHandler())
     * }</pre>
     *
     * @param firstHandler the first handler
     * @return a new parser of which first handler is the given handler and the next handler is this parser as a
     * {@link Handler}
     */
    default @Nonnull ObjectParser withFirstHandler(@Nonnull Handler firstHandler) {
        return newParser(firstHandler, this.asHandler());
    }

    /**
     * Returns this parser as a {@link Handler}.
     *
     * @return this parser as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link ObjectParser}, provides the specific parsing logic.
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
