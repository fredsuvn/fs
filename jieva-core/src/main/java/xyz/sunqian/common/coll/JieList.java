package xyz.sunqian.common.coll;

import java.util.List;

/**
 * Static utility class for list.
 *
 * @author sunqian
 */
public class JieList {

    public static <T> List<T> list(T... elements) {
        return ListBack.immutableList(elements);
    }
}
