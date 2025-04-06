import java.util.List;
/**
 * Defines the contract for product-related operations, ensuring consistency and testability.
 * This interface is designed to facilitate unit testing by providing a structured set of methods.
 */
public interface InterfaceProductFunctions {
    /**
     * Retrieves the list of all available products.
     *
     * @return A list of {@link Product} objects representing the available products.
     */
    List<Product> viewProducts();
    /**
     * Adds a new product to the inventory.
     *
     * @param product The {@link Product} object representing the product to be added.
     */
    void addProduct(Product product);
    /**
     * Edits an existing product in the inventory.
     *
     * @param productId The unique identifier of the product to be edited.
     * @param newName The new name to update the product with.
     * @param newStock The new stock quantity for the product.
     * @param newPrice The new price for the product.
     */
    void editProduct(int productId, String newName, int newStock, double newPrice);
    /**
     * Deletes a product from the inventory based on its ID.
     *
     * @param productId The unique identifier of the product to be deleted.
     */
    void deleteProduct(int productId);

    List<String> getDistinctValues(String column, String typeFilter); //BlackBoxTesting
}
