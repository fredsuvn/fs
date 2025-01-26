package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.objects.handlers.AbstractBeanResolverHandler;
import xyz.sunqian.common.objects.handlers.JavaBeanResolverHandler;
import xyz.sunqian.common.objects.handlers.NonGetterPrefixResolverHandler;
import xyz.sunqian.common.objects.handlers.NonPrefixResolverHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * Introspector for {@link ObjectDef}, typically consists of a {@link Handler} list. There are 3 built-in
 * introspectors:
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
public interface ObjectIntrospector {

    /**
     * Returns default bean introspector of which handler is {@link JavaBeanResolverHandler}.
     *
     * @return default bean introspector
     */
    static ObjectIntrospector defaultResolver() {
        return ObjectIntrospectorImpl.SINGLETON;
    }

    /**
     * Returns new bean introspector with given handlers.
     *
     * @param handlers given handlers
     * @return new bean introspector
     */
    static ObjectIntrospector withHandlers(Handler... handlers) {
        return withHandlers(JieArray.asList(handlers));
    }

    /**
     * Returns new bean introspector with given handlers.
     *
     * @param handlers given handlers
     * @return new bean introspector
     */
    static ObjectIntrospector withHandlers(Iterable<Handler> handlers) {
        return new ObjectIntrospectorImpl(handlers);
    }

    /**
     * Introspects specified object type to {@link ObjectDef}.
     *
     * @param type specified object type
     * @return the {@link ObjectDef}
     * @throws ObjectIntrospectionException if any problem occurs
     */
    ObjectDef introspect(Type type) throws ObjectIntrospectionException;

    /**
     * Returns handlers of this introspector.
     *
     * @return handlers of this introspector
     */
    @Immutable
    List<Handler> getHandlers();

    /**
     * Returns a new {@link ObjectIntrospector} of which handler list consists of given handler as first element,
     * followed by {@link #getHandlers()} of current introspector.
     *
     * @param handler given handler
     * @return a new {@link ObjectIntrospector} of which handler list consists of given handler as first element,
     * followed by {@link #getHandlers()} of current introspector
     */
    ObjectIntrospector addFirstHandler(Handler handler);

    /**
     * Returns a new {@link ObjectIntrospector} of which handler list consists of {@link #getHandlers()} of current
     * introspector, followed by given handler as last element.
     *
     * @param handler given handler
     * @return a {@link ObjectIntrospector} of which handler list consists of {@link #getHandlers()} of current
     * introspector, followed by given handler as last element
     */
    ObjectIntrospector addLastHandler(Handler handler);

    /**
     * Returns a new {@link ObjectIntrospector} of which handler list comes from a copy of {@link #getHandlers()} of
     * current introspector but the first element is replaced by given handler.
     * <p>
     * Note if replaced handler equals given handler, return this-self.
     *
     * @param handler given handler
     * @return a new {@link ObjectIntrospector} of which handler list comes from a copy of {@link #getHandlers()} of
     * current introspector but the first element is replaced by given handler
     */
    ObjectIntrospector replaceFirstHandler(Handler handler);

    /**
     * Returns a new {@link ObjectIntrospector} of which handler list comes from a copy of {@link #getHandlers()} of
     * current introspector but the last element is replaced by given handler.
     * <p>
     * Note if replaced handler equals given handler, return this-self.
     *
     * @param handler given handler
     * @return a new {@link ObjectIntrospector} of which handler list comes from a copy of {@link #getHandlers()} of
     * current introspector but the last element is replaced by given handler
     */
    ObjectIntrospector replaceLastHandler(Handler handler);

    /**
     * Returns this introspector as {@link Handler}.
     *
     * @return this introspector as {@link Handler}
     */
    Handler asHandler();

    /**
     * Object introspection handler of {@link ObjectIntrospector}.
     *
     * @author sunqian
     * @see ObjectIntrospector
     */
    @ThreadSafe
    interface Handler {

        /**
         * Introspects the type of specified object via the given context.
         * <p>
         * When the {@link ObjectIntrospector} starts an introspection process, this method will be invoked sequentially
         * for each handler returned by {@link #getHandlers()}. If it returns {@code true}, the next handler will be
         * invoked as usual. If it returns {@code false}, the next handler will not be invoked and introspection process
         * will be terminated.
         *
         * @param context given context
         */
        @Nullable
        boolean introspect(Context context);
    }

    /**
     * The context of object introspection.
     *
     * @author sunqian
     * @see ObjectIntrospector
     */
    interface Context {

        /**
         * Returns type of the object to be introspected.
         *
         * @return type of the object to be introspected
         */
        Type getObjectType();

        /**
         * Returns a mutable map for storing property introspection info in current introspection context.
         * <p>
         * The map through whole introspection process, store and share the property info for all handlers, and each
         * handler can add or remove or modify the property info.
         *
         * @return a mutable map for storing property introspection info in current introspection context
         */
        Map<String, PropertyIntro> propertyIntros();

        /**
         * Returns a mutable list for storing method introspection info in current introspection context.
         * <p>
         * The list through whole introspection process, store and share the method info for all handlers, and each
         * handler can add or remove or modify the method info.
         *
         * @return a mutable list for storing method introspection info in current introspection context
         */
        List<MethodIntro> methodIntros();
    }
}
