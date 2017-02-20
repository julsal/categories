package unipassau.categories.experiment;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class Utils {

    public static <K, V extends Comparable> Map<K, V> reverse(Map<K, V> map, int limit) {
        return sortByValues(map, limit, true);
    }

    public static <K, V extends Comparable> Map<K, V> reverse(Map<K, V> map) {
        return sortByValues(map, -1, true);
    }

    public static <K, V extends Comparable> Map<K, V> sort(Map<K, V> map) {
        return sortByValues(map, -1, false);
    }

    public static <K, V extends Comparable> Map<K, V> sortByValues(Map<K, V> map, int limit, boolean reversed) {
        List<Map.Entry<K, V>> entries = new LinkedList<>(map.entrySet());

        Comparator<Map.Entry<K, V>> comparator
                = (o1, o2) -> o1.getValue().compareTo(o2.getValue());

        if (reversed) {
            comparator = comparator.reversed();
        }

        if (limit > 0) {
            entries = entries.stream()
                    .sorted(comparator)
                    .limit(limit).collect(Collectors.toList());
        } else {
            entries = entries.stream()
                    .sorted(comparator)
                    .collect(Collectors.toList());
        }

        Map<K, V> sortedMap = new LinkedHashMap<>();
        for (Map.Entry<K, V> entry : entries) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        return sortedMap;
    }

    public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(Function<? super T, ? extends K> keyMapper,
                                                                   Function<? super T, ? extends U> valueMapper) {
        return Collectors.toMap(keyMapper, valueMapper,
                (u, v) -> {
                    throw new IllegalStateException(String.format("Duplicate key %s", u));
                },
                LinkedHashMap::new);
    }
}
