import java.util.Collection;

/**
 * A generic interface for searching an inventory or collection.
 *
 * @param <E> The type of elements in the collection.
 * @param <V> The type of the search value.
 */
public interface Searchable<E, V> {
    /**
     * Searches an underlying inventory for items matching value.
     * @param value The search value.
     * @return A Collection of matching items.
     */
    public Collection<E> search(V value);
}
