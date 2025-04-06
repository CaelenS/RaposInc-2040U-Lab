import java.util.Collection;

/**
 * Implements the {@link Searchable} interface to provide database-backed search functionality.
 * This class retrieves distinct values from a specified database column based on a search query.
 */
public class DatabaseSearchable implements Searchable<String, String> {
    
    /**
     * The database column to search within.
     */
    private final String column;
    
    /**
     * The {@link InterfaceProductFunctions} instance responsible for retrieving data from the database.
     */
    private final InterfaceProductFunctions productFunctions;
    
    /**
     * Constructs a new {@code DatabaseSearchable} with the specified column and database handler.
     *
     * @param column The database column to perform searches on.
     * @param productFunctions The product functions instance used to retrieve data.
     */
    public DatabaseSearchable(String column, InterfaceProductFunctions productFunctions) {
        this.column = column;
        this.productFunctions = productFunctions;
    }
    
    /**
     * Searches the specified database column for distinct values that match the given query.
     *
     * @param value The search query.
     * @return A collection of distinct values matching the search criteria.
     */
    @Override
    public Collection<String> search(String value) {
        // Delegate search to the getDistinctValues function.
        return productFunctions.getDistinctValues(column, value);
    }
}
