import java.util.Collection;

public interface Searchable<E, V> {
    /**
     * Searches an underlying inventory for items matching value.
     * @param value The search value.
     * @return A Collection of matching items.
     */
    public Collection<E> search(V value);
}