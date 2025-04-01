import java.util.List;
/**
 * Defines the contract for product-related operations, ensuring consistency and testability.
 * This interface is designed to facilitate unit testing by providing a structured set of methods.
 */
public interface InterfaceProductFunctions {
    List<Product> viewProducts();
    void addProduct(Product product);
    void editProduct(int productId, String newName, int newStock, double newPrice);
    void deleteProduct(int productId);
}
