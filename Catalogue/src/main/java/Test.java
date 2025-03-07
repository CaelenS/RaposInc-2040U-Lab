import java.sql.Connection;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        Connection conn = DatabaseConnection.connect();
        if (conn == null) return;

        ProductFunctions database = new ProductFunctions(conn);

        // Add a product
//        Product newProduct = new Product(0, "Board Game X", 30, 29.99, "Board Game", 4.5, "FunGames", "555555555555", "Exciting family game.");
//
//        database.addProduct(newProduct);

        // View all products
        List<Product> products = database.viewProducts();
        products.forEach(p -> System.out.println(p.productName + " - $" + p.price));

        //️ Edit a product
//        database.editProduct(1, "Updated Chess Set", 25, 27.99);

        // Delete a product
//        database.deleteProduct(2);
    }
}
