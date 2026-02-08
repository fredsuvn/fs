package space.sunqian.fs.collect;

import space.sunqian.annotation.Nonnull;
import space.sunqian.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.AbstractList;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.IntFunction;

final class CollectBack {

    static @Nullable IntFunction<@Nonnull Map<?, ?>> mapFunction(@Nonnull Type target) {
        return MapClasses.CLASS_MAP.get(target);
    }

    static @Nullable IntFunction<@Nonnull Set<?>> setFunction(@Nonnull Type target) {
        return SetClasses.CLASS_MAP.get(target);
    }

    static @Nullable IntFunction<@Nonnull List<?>> listFunction(@Nonnull Type target) {
        return ListClasses.CLASS_MAP.get(target);
    }

    private static final class MapClasses {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull IntFunction<@Nonnull Map<?, ?>>> CLASS_MAP;

        static {
            CLASS_MAP = new HashMap<>();
            CLASS_MAP.put(Map.class, HashMap::new);
            CLASS_MAP.put(AbstractMap.class, HashMap::new);
            CLASS_MAP.put(HashMap.class, HashMap::new);
            CLASS_MAP.put(LinkedHashMap.class, LinkedHashMap::new);
            CLASS_MAP.put(TreeMap.class, size -> new TreeMap<>());
            CLASS_MAP.put(ConcurrentMap.class, ConcurrentHashMap::new);
            CLASS_MAP.put(ConcurrentHashMap.class, ConcurrentHashMap::new);
            CLASS_MAP.put(Hashtable.class, Hashtable::new);
            CLASS_MAP.put(ConcurrentSkipListMap.class, size -> new ConcurrentSkipListMap<>());
        }
    }

    private static final class SetClasses {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull IntFunction<@Nonnull Set<?>>> CLASS_MAP;

        static {
            CLASS_MAP = new HashMap<>();
            CLASS_MAP.put(Iterable.class, HashSet::new);
            CLASS_MAP.put(Collection.class, HashSet::new);
            CLASS_MAP.put(Set.class, HashSet::new);
            CLASS_MAP.put(AbstractSet.class, HashSet::new);
            CLASS_MAP.put(HashSet.class, HashSet::new);
            CLASS_MAP.put(LinkedHashSet.class, LinkedHashSet::new);
            CLASS_MAP.put(TreeSet.class, size -> new TreeSet<>());
            CLASS_MAP.put(CopyOnWriteArraySet.class, size -> new CopyOnWriteArraySet<>());
            CLASS_MAP.put(ConcurrentSkipListSet.class, size -> new ConcurrentSkipListSet<>());
        }
    }

    private static final class ListClasses {

        private static final @Nonnull Map<@Nonnull Type, @Nonnull IntFunction<@Nonnull List<?>>> CLASS_MAP;

        static {
            CLASS_MAP = new HashMap<>();
            CLASS_MAP.put(Iterable.class, ArrayList::new);
            CLASS_MAP.put(Collection.class, ArrayList::new);
            CLASS_MAP.put(List.class, ArrayList::new);
            CLASS_MAP.put(AbstractList.class, ArrayList::new);
            CLASS_MAP.put(ArrayList.class, ArrayList::new);
            CLASS_MAP.put(LinkedList.class, size -> new LinkedList<>());
            CLASS_MAP.put(CopyOnWriteArrayList.class, size -> new CopyOnWriteArrayList<>());
        }
    }

    private CollectBack() {
    }
}
