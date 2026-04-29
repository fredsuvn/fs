package space.sunqian.fs.object.meta;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.RetainedParam;
import space.sunqian.annotation.ThreadSafe;
import space.sunqian.fs.cache.SimpleCache;
import space.sunqian.fs.collect.ListKit;
import space.sunqian.fs.object.meta.handlers.AbstractObjectMetaHandler;
import space.sunqian.fs.object.meta.handlers.CommonMetaHandler;
import space.sunqian.fs.object.meta.handlers.RecordMetaHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to parse {@link Type} to {@link ObjectMeta}. It uses a list of {@link Handler}s to execute
 * the specific parsing operations, where each {@link Handler} possesses its own specific parsing logic.
 * <p>
 * There is a skeletal handler implementation: {@link AbstractObjectMetaHandler}. And the default parser is based on
 * {@link CommonMetaHandler#getInstance()}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface ObjectMetaManager {

    /**
     * Returns the default {@link ObjectMetaManager}. Here are handlers in the default parser:
     * <ul>
     *     <li>
     *         {@link RecordMetaHandler#getInstance()}, if the current JVM version supports {@code record} classes
     *         ({@code >= 16});
     *     </li>
     *     <li>{@link CommonMetaHandler#getInstance()};</li>
     * </ul>
     * <p>
     * Note the default {@link ObjectMetaManager} is singleton, and never caches the parsed results.
     *
     * @return the default {@link ObjectMetaManager}
     * @see CommonMetaHandler
     * @see RecordMetaHandler
     */
    static @Nonnull ObjectMetaManager defaultManager() {
        return ObjectMetaBack.defaultParser();
    }

    /**
     * Returns the default cached {@link ObjectMetaManager}, which is based on {@link #defaultManager()} and caches the
     * parsed results with {@link SimpleCache#ofSoft()}.
     * <p>
     * Note the default cached {@link ObjectMetaManager} is singleton.
     *
     * @return the default cached {@link ObjectMetaManager}
     * @see #defaultManager()
     */
    static @Nonnull ObjectMetaManager defaultCachedManager() {
        return ObjectMetaBack.defaultCachedParser();
    }

    /**
     * Creates and returns a new {@link ObjectMetaManager} with the given handlers.
     * <p>
     * Note the created {@link ObjectMetaManager} never caches the parsed results.
     *
     * @param handlers the given handlers
     * @return a new {@link ObjectMetaManager} with the given handlers
     */
    static @Nonnull ObjectMetaManager newManager(@Nonnull @RetainedParam Handler @Nonnull ... handlers) {
        return newManager(ListKit.list(handlers));
    }

    /**
     * Creates and returns a new {@link ObjectMetaManager} with given handlers.
     * <p>
     * Note the created {@link ObjectMetaManager} never caches the parsed results.
     *
     * @param handlers given handlers
     * @return a new {@link ObjectMetaManager} with given handlers
     */
    static @Nonnull ObjectMetaManager newManager(@Nonnull @RetainedParam List<@Nonnull Handler> handlers) {
        return ObjectMetaBack.newParser(handlers);
    }

    /**
     * Returns a new {@link ObjectMetaManager} that caches the parsed results with the specified cache.
     * <p>
     * Note the behavior of the non-parsing methods of the returned {@link ObjectMetaManager}, such as
     * {@link #handlers()}, {@link #asHandler()} and {@link #withFirstHandler(Handler)}, will directly invoke the
     * underlying {@link ObjectMetaManager}.
     *
     * @param cache  the specified cache to store the parsed results
     * @param parser the underlying {@link ObjectMetaManager} to parse the type
     * @return a new {@link ObjectMetaManager} that caches the parsed results with the specified cache
     */
    static @Nonnull ObjectMetaManager newManager(
        @Nonnull SimpleCache<@Nonnull Type, @Nonnull ObjectMeta> cache,
        @Nonnull ObjectMetaManager parser
    ) {
        return ObjectMetaBack.newCachedParser(cache, parser);
    }

    /**
     * Parses the given type to an instance of {@link ObjectMeta}, and returns the parsed {@link ObjectMeta}.
     *
     * @param type the given type
     * @return the parsed {@link ObjectMeta}
     * @throws DataMetaException if any problem occurs
     * @implNote The default implementation of this method invokes the {@link Handler#parse(ObjectMetaManager.Context)}
     * in the order of {@link #handlers()} until one of the handlers returns {@code false}. The codes are similar to:
     * <pre>{@code
     * for (Handler handler : handlers()) {
     *     if (!handler.parse(context)) {
     *         break;
     *     }
     * }
     * }</pre>
     */
    default @Nonnull ObjectMeta parse(@Nonnull Type type) throws DataMetaException {
        try {
            ObjectMetaBuilder builder = new ObjectMetaBuilder(type);
            for (Handler handler : handlers()) {
                if (!handler.parse(builder)) {
                    break;
                }
            }
            return builder.build(this);
        } catch (Exception e) {
            throw new DataMetaException(type, e);
        }
    }

    /**
     * Returns all handlers of this {@link ObjectMetaManager}.
     *
     * @return all handlers of this {@link ObjectMetaManager}
     */
    @Nonnull
    List<@Nonnull Handler> handlers();

    /**
     * Returns a new {@link ObjectMetaManager} of which first handler is the given handler and the next handler is this
     * {@link ObjectMetaManager} as a {@link Handler}. This method is equivalent:
     * <pre>{@code
     * newParser(firstHandler, this.asHandler())
     * }</pre>
     *
     * @param firstHandler the first handler
     * @return a new {@link ObjectMetaManager} of which first handler is the given handler and the next handler is this
     * {@link ObjectMetaManager} as a {@link Handler}
     */
    default @Nonnull ObjectMetaManager withFirstHandler(@Nonnull Handler firstHandler) {
        return newManager(firstHandler, this.asHandler());
    }

    /**
     * Returns this {@link ObjectMetaManager} as a {@link Handler}.
     *
     * @return this {@link ObjectMetaManager} as a {@link Handler}
     */
    @Nonnull
    Handler asHandler();

    /**
     * Handler for {@link ObjectMetaManager}, provides the specific parsing logic.
     *
     * @author sunqian
     */
    @ThreadSafe
    interface Handler {

        /**
         * Parses {@link Type} to {@link ObjectMeta} with its owner parsing logic. The {@link Type} is specified by
         * {@link Context#parsedType()} of the given context, and the parsed properties should be stored in
         * {@link Context#propertyBaseMap()}. Returns {@code false} to prevent subsequent handlers to continue to parse,
         * otherwise returns {@code true} to continue to parse.
         *
         * @param context the given context
         * @return whether to continue to parse
         * @throws Exception for errors during parsing
         */
        boolean parse(@Nonnull Context context) throws Exception;
    }

    /**
     * Context for parsing the specified {@link Type} to {@link ObjectMeta}.
     *
     * @author sunqian
     */
    interface Context {

        /**
         * Returns the type to be parsed.
         *
         * @return the type to be parsed
         */
        @Nonnull
        Type parsedType();

        /**
         * Returns a mutable map for storing property base infos.
         * <p>
         * The map through whole parsing process, stores and shares the property base infos for all handlers, and each
         * handler can add or remove or modify the property base info.
         *
         * @return a mutable map for storing property base infos
         */
        @Nonnull
        Map<@Nonnull String, @Nonnull PropertyMetaBase> propertyBaseMap();
    }
}
