import java.util.Collection;

public class DatabaseSearchable implements Searchable<String, String> {
    private final String column;
    private final ProductFunctions productFunctions;
    
    public DatabaseSearchable(String column, ProductFunctions productFunctions) {
        this.column = column;
        this.productFunctions = productFunctions;
    }
    
    @Override
    public Collection<String> search(String value) {
        // Delegate search to your getDistinctValues function.
        return productFunctions.getDistinctValues(column, value);
    }
}