package xyz.sunqian.common.base.coding;

import xyz.sunqian.annotations.Nonnull;
import xyz.sunqian.annotations.Nullable;
import xyz.sunqian.common.base.Jie;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Utilities for coding.
 *
 * @author sunqian
 */
public class CodingKit {

    /**
     * If the {@code old} is null, return the {@code added}. If the {@code added} is null, return the {@code old}. If
     * the {@code old} is a collection, adding the {@code added} into the {@code old}, returns the {@code old}.
     * Otherwise, returns a new {@link ArrayList} contains the {@code old} and {@code added}.
     *
     * @param old   the old
     * @param added the added
     * @return the {@code added} or a new {@link ArrayList} contains the {@code old} and {@code added}
     */
    public static Object ifAdd(@Nullable Object old, @Nullable Object added) {
        if (old == null) {
            return added;
        }
        if (added == null) {
            return old;
        }
        if (old instanceof Collection) {
            Collection<Object> oldList = Jie.as(old);
            oldList.add(added);
            return old;
        }
        List<Object> list = new ArrayList<>(2);
        list.add(old);
        list.add(added);
        return list;
    }

    /**
     * If the {@code objOrColl} is null, returns the result of the {@code merger} with an empty collection. If the
     * {@code objOrColl} is a collection, returns the result of the {@code merger} with the collection. Otherwise, this
     * method returns the result of the {@code merger} with the collection which has a singleton element:
     * {@code objOrColl}.
     *
     * @param objOrColl the {@code objOrColl}
     * @param merger    the {@code merger}
     * @param <T>       component type of the collection
     * @return the result of the {@code merger}
     */
    public static <T> T ifMerge(@Nullable Object objOrColl, @Nonnull Function<Collection<T>, T> merger) {
        if (objOrColl == null) {
            return merger.apply(Collections.emptyList());
        }
        if (objOrColl instanceof Collection<?>) {
            Collection<T> collection = Jie.as(objOrColl);
            return merger.apply(collection);
        }
        return merger.apply(Collections.singletonList(Jie.as(objOrColl)));
    }
}
