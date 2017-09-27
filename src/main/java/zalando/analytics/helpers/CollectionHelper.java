package zalando.analytics.helpers;

import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Set of methods to help with collections and maps.
 */
public class CollectionHelper {

    /**
     * Sort this map by value, descending.
     *
     * @param map Map to be sorted
     * @return Map sorted by value, descending.
     */
    public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortMapByValueDesc(
            Map<K, V> map) {
        return sortMapByValue(map, false);
    }

    /**
     * Sort this map by value, ascending.
     *
     * @param map Map to be sorted
     * @return Map sorted by value, ascending.
     */
    public static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortMapByValueAsc(
            Map<K, V> map) {
        return sortMapByValue(map, true);
    }

    /**
     * Sort this map by value.
     *
     * @param map Map to be sorted
     * @return Map sorted by value.
     */
    private static <K, V extends Comparable<? super V>> SortedSet<Map.Entry<K, V>> sortMapByValue(
            Map<K, V> map, boolean asc) {

        final int sortingKey = (asc) ? 1 : -1;

        SortedSet<Map.Entry<K, V>> mapSorted = new TreeSet<>(
                (e1, e2) -> {
                    if (e1.getValue().compareTo(e2.getValue()) == 0)
                        return 1;
                    return sortingKey * e1.getValue().compareTo(e2.getValue());
                });
        mapSorted.addAll(map.entrySet());
        return mapSorted;
    }
}