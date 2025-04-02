import java.util.Collection;

public class DatabaseSearchable implements Searchable<String, String> {
    private final String column;
    private final InterfaceProductFunctions productFunctions; //BlackBoxTests
    
    public DatabaseSearchable(String column, InterfaceProductFunctions productFunctions) { //BlackBoxTests
        this.column = column;
        this.productFunctions = productFunctions;
    }
    
    @Override
    public Collection<String> search(String value) {
        // Delegate search to your getDistinctValues function.
        return productFunctions.getDistinctValues(column, value);
    }
}