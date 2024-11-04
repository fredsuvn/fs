package xyz.sunqian.common.bean;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.base.Flag;
import xyz.sunqian.common.bean.handlers.AbstractBeanResolverHandler;
import xyz.sunqian.common.bean.handlers.JavaBeanResolverHandler;
import xyz.sunqian.common.bean.handlers.NonGetterPrefixResolverHandler;
import xyz.sunqian.common.bean.handlers.NonPrefixResolverHandler;
import xyz.sunqian.common.coll.JieArray;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Resolver for {@link BeanInfo}, usually consists of a {@link Handler} list. There are 3 built-in resolvers:
 * <ul>
 *     <li>{@link JavaBeanResolverHandler} (default handler);</li>
 *     <li>{@link NonGetterPrefixResolverHandler};</li>
 *     <li>{@link NonPrefixResolverHandler};</li>
 * </ul>
 * And a skeletal implementation: {@link AbstractBeanResolverHandler}.
 *
 * @author fredsuvn
 */
@ThreadSafe
public interface BeanResolver {

    /**
     * Returns default bean resolver of which handler is {@link JavaBeanResolverHandler}.
     *
     * @return default bean resolver
     */
    static BeanResolver defaultResolver() {
        return BeanResolverImpl.DEFAULT_RESOLVER;
    }

    /**
     * Returns new bean resolver with given handlers.
     *
     * @param handlers given handlers
     * @return new bean resolver
     */
    static BeanResolver withHandlers(Handler... handlers) {
        return withHandlers(JieArray.asList(handlers));
    }

    /**
     * Returns new bean resolver with given handlers.
     *
     * @param handlers given handlers
     * @return new bean resolver
     */
    static BeanResolver withHandlers(Iterable<Handler> handlers) {
        return new BeanResolverImpl(handlers);
    }

    /**
     * Resolves given type to {@link BeanInfo}.
     *
     * @param type given type
     * @return resolved {@link BeanInfo}
     * @throws BeanResolvingException if any problem occurs when resolving
     */
    BeanInfo resolve(Type type) throws BeanResolvingException;

    /**
     * Returns handlers of this resolver.
     *
     * @return handlers of this resolver
     */
    @Immutable
    List<Handler> getHandlers();

    /**
     * Returns a new {@link BeanResolver} of which handler list consists of given handler as first element, followed by
     * {@link #getHandlers()} of current resolver.
     *
     * @param handler given handler
     * @return a new {@link BeanResolver} of which handler list consists of given handler as first element, followed by
     * {@link #getHandlers()} of current resolver
     */
    BeanResolver addFirstHandler(Handler handler);

    /**
     * Returns a new {@link BeanResolver} of which handler list consists of {@link #getHandlers()} of current resolver,
     * followed by given handler as last element.
     *
     * @param handler given handler
     * @return a {@link BeanResolver} of which handler list consists of {@link #getHandlers()} of current resolver,
     * followed by given handler as last element
     */
    BeanResolver addLastHandler(Handler handler);

    /**
     * Returns a new {@link BeanResolver} of which handler list comes from a copy of {@link #getHandlers()} of current
     * resolver but the first element is replaced by given handler.
     * <p>
     * Note if replaced handler equals given handler, return this-self.
     *
     * @param handler given handler
     * @return a new {@link BeanResolver} of which handler list comes from a copy of {@link #getHandlers()} of current
     * resolver but the first element is replaced by given handler
     */
    BeanResolver replaceFirstHandler(Handler handler);

    /**
     * Returns a new {@link BeanResolver} of which handler list comes from a copy of {@link #getHandlers()} of current
     * resolver but the last element is replaced by given handler.
     * <p>
     * Note if replaced handler equals given handler, return this-self.
     *
     * @param handler given handler
     * @return a new {@link BeanResolver} of which handler list comes from a copy of {@link #getHandlers()} of current
     * resolver but the last element is replaced by given handler
     */
    BeanResolver replaceLastHandler(Handler handler);

    /**
     * Returns this resolver as {@link Handler}.
     *
     * @return this resolver as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link BeanResolver}.
     *
     * @author fredsuvn
     * @see BeanResolver
     */
    @ThreadSafe
    interface Handler {

        /**
         * Resolves type of bean from given context.
         * <p>
         * Properties map from {@link Context#getProperties()} and methods list from {@link Context#getMethods()} are
         * mutable. Each handler can operate the map/list -- adding or removing or modifying the content of maps which
         * is resolved by previous handlers.
         * <p>
         * This method will be invoked for each handler from {@link #getHandlers()} sequentially. If a handler want to
         * prevent the remaining resolving, return {@link Flag#BREAK} to break invoking-sequence. Other returned value
         * will be ignored.
         *
         * @param context given resolving context
         * @see BeanResolver
         */
        @Nullable
        Flag resolve(Context context);
    }

    /**
     * Context for resolving of {@link BeanResolver}.
     * <p>
     * The context includes a mutable properties map {@link #getProperties()} and a mutable methods list
     * {@link #getMethods()}, resolving handlers can modify the content of maps.
     *
     * @author fredsuvn
     * @see BeanResolver
     */
    interface Context {

        /**
         * Returns type of bean.
         *
         * @return type of bean
         */
        Type getType();

        /**
         * Returns properties map in current resolving context.
         * <p>
         * The map through whole resolving process, store and share all base property info for all handlers. Each
         * handler can add or remove or modify base property info which is resolved by previous handlers in this map.
         *
         * @return properties map in current resolving context
         */
        Map<String, BasePropertyInfo> getProperties();

        /**
         * Returns methods list in current resolving context.
         * <p>
         * The list through whole resolving process, store and share all base method info for all handlers. Each handler
         * can add or remove or modify base method info which is resolved by previous handlers in this list.
         *
         * @return methods map in current resolving context
         */
        List<BaseMethodInfo> getMethods();
    }
}
