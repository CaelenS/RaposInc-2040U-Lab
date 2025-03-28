import java.util.List;
// Interface for all the functions of the product.
// Made to ensure unit testing did not encounter errors.
public interface InterfaceProductFunctions {
    List<Product> viewProducts();
    void addProduct(Product product);
    void editProduct(int productId, String newName, int newStock, double newPrice);
    void deleteProduct(int productId);
}
