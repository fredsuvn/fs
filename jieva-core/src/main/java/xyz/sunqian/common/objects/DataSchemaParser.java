package xyz.sunqian.common.objects;

import xyz.sunqian.annotations.Immutable;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.annotations.RetainedParam;
import xyz.sunqian.annotations.ThreadSafe;
import xyz.sunqian.common.coll.JieArray;
import xyz.sunqian.common.objects.handlers.AbstractDataSchemaHandler;
import xyz.sunqian.common.objects.handlers.JavaBeanDataSchemaHandler;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * This interface is used to parse {@link Type} of the data object to {@link DataSchema}. It typically consists of a
 * list of {@link Handler}, where each {@link Handler} provides its own parsing behavior.
 * <p>
 * There is a skeletal handler implementation: {@link AbstractDataSchemaHandler}. And the default parser is based on
 * {@link JavaBeanDataSchemaHandler}.
 *
 * @author sunqian
 */
@ThreadSafe
public interface DataSchemaParser {

    /**
     * Returns default {@link DataSchemaParser}, of which handler is {@link JavaBeanDataSchemaHandler}.
     *
     * @return default {@link DataSchemaParser}
     */
    static DataSchemaParser defaultParser() {
        return DataSchemaParserImpl.SINGLETON;
    }

    /**
     * Creates and returns a new {@link DataSchemaParser} with given handlers.
     *
     * @param handlers given handlers
     * @return a new {@link DataSchemaParser} with given handlers
     */
    static DataSchemaParser withHandlers(@RetainedParam Handler... handlers) {
        return withHandlers(JieArray.listOf(handlers));
    }

    /**
     * Creates and returns a new {@link DataSchemaParser} with given handlers.
     *
     * @param handlers given handlers
     * @return a new {@link DataSchemaParser} with given handlers
     */
    static DataSchemaParser withHandlers(@RetainedParam List<? extends Handler> handlers) {
        return new DataSchemaParserImpl(handlers);
    }

    /**
     * Returns the {@link DataSchema} parsed from the specified type.
     *
     * @param type specified type
     * @return the {@link DataSchema}
     * @throws DataObjectException if any problem occurs
     */
    DataSchema parse(Type type) throws DataObjectException;

    /**
     * Returns all handlers of this parser.
     *
     * @return all handlers of this parser
     */
    @Immutable
    List<Handler> getHandlers();

    /**
     * Returns a new {@link DataSchemaParser} of which handler list consists of given handler as first element, followed
     * by {@link #getHandlers()} of current parser.
     *
     * @param handler given handler
     * @return a new {@link DataSchemaParser} of which handler list consists of given handler as first element, followed
     * by {@link #getHandlers()} of current parser
     */
    default DataSchemaParser addFirstHandler(Handler handler) {
        Handler[] newHandlers = new Handler[getHandlers().size() + 1];
        int i = 0;
        newHandlers[i++] = handler;
        for (Handler h : getHandlers()) {
            newHandlers[i++] = h;
        }
        return withHandlers(newHandlers);
    }

    /**
     * Returns a new {@link DataSchemaParser} of which handler list consists of {@link #getHandlers()} of current
     * parser, followed by given handler as last element.
     *
     * @param handler given handler
     * @return a {@link DataSchemaParser} of which handler list consists of {@link #getHandlers()} of current parser,
     * followed by given handler as last element
     */
    default DataSchemaParser addLastHandler(Handler handler) {
        Handler[] newHandlers = new Handler[getHandlers().size() + 1];
        int i = 0;
        for (Handler h : getHandlers()) {
            newHandlers[i++] = h;
        }
        newHandlers[i] = handler;
        return withHandlers(newHandlers);
    }

    /**
     * Returns this parser as {@link Handler}.
     *
     * @return this parser as {@link Handler}
     */
    Handler asHandler();

    /**
     * Handler of {@link DataSchemaParser}, provides the specific data schema parsing behavior.
     *
     * @author sunqian
     * @see DataSchemaParser
     */
    @ThreadSafe
    interface Handler {

        /**
         * Parses the type to the data schema via given parsing context.
         * <p>
         * When the {@link DataSchemaParser} starts the parsing process, this method will be invoked sequentially for
         * each handler in {@link #getHandlers()}. If it returns {@code true}, the next handler will be invoked as
         * usual. If it returns {@code false}, the next handler will not be invoked and the parsing process will be
         * terminated.
         *
         * @param context given parsing context
         */
        @Nullable
        boolean doParse(Context context);
    }

    /**
     * The {@link DataSchema} parsing context.
     *
     * @author sunqian
     * @see DataSchemaParser
     */
    interface Context {

        /**
         * Returns the type to be parsed.
         *
         * @return the type to be parsed
         */
        Type getType();

        /**
         * Returns a mutable map for storing property base infos.
         * <p>
         * The map through whole parsing process, stores and shares the property base infos for all handlers, and each
         * handler can add or remove or modify the property base info.
         *
         * @return a mutable map for storing property base infos
         */
        Map<String, DataPropertyBase> getPropertyBaseMap();
    }
}
