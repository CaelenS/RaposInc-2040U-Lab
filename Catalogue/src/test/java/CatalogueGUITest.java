import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CatalogueGUITest {
    private ProductCatalogueGUI gui;
    private FakeProductFunctions fakeProductFunctions;

    @BeforeEach
    public void setUp() throws Exception {
        Connection conn = DatabaseConnection.connect();
        UserAuth auth = new UserAuth(conn);
        gui = new ProductCatalogueGUI(auth.login("admin", "admin123"));
        fakeProductFunctions = new FakeProductFunctions();
    
        // Replace the REAL productFunctions with the FAKE version.
        Field productFunctionsField = ProductCatalogueGUI.class.getDeclaredField("productFunctions");
        productFunctionsField.setAccessible(true);
        productFunctionsField.set(gui, fakeProductFunctions);  // Inject fake dependency.
    }

    // Unit Tests.
    @Test
    public void testLoadProducts() throws Exception {
        // Make fake test product.
        List<Product> products = new ArrayList<>();
        Product testProduct = new Product(1, "Test", 10, 999.99, 
                                        "Electronics", 4.5, "TestBrand", 
                                        "123456789", "Test description");
        products.add(testProduct);
        fakeProductFunctions.setFakeProducts(products);

        // Invoke loadProducts() using reflection.
        Method loadProductsMethod = ProductCatalogueGUI.class.getDeclaredMethod("loadProducts");
        loadProductsMethod.setAccessible(true);
        loadProductsMethod.invoke(gui);

        // Verify table contents.
        Field tableModelField = ProductCatalogueGUI.class.getDeclaredField("tableModel");
        tableModelField.setAccessible(true);
        DefaultTableModel model = (DefaultTableModel) tableModelField.get(gui);

        assertEquals(1, model.getRowCount(), "Should have 1 product row");
        assertEquals(1, model.getValueAt(0, 0), "Product ID mismatch");
        assertEquals("Test", model.getValueAt(0, 1), "Product name mismatch");
        assertEquals(10, model.getValueAt(0, 2), "Stock mismatch");
        assertEquals(999.99, model.getValueAt(0, 3), "Price mismatch");
    }

    @Test
    public void testDeleteProduct() throws Exception {
        // Setup fake test the product.
        List<Product> products = new ArrayList<>();
        Product testProduct = new Product(1, "Test", 50, 29.99, 
                                        "Genre", 4.0, "TestBrand", 
                                        "987654321", "Test description");
        products.add(testProduct);
        fakeProductFunctions.setFakeProducts(products);

        // Load products first.
        Method loadProductsMethod = ProductCatalogueGUI.class.getDeclaredMethod("loadProducts");
        loadProductsMethod.setAccessible(true);
        loadProductsMethod.invoke(gui);

        // Simulate row selection.
        Field tableField = ProductCatalogueGUI.class.getDeclaredField("table");
        tableField.setAccessible(true);
        JTable table = (JTable) tableField.get(gui);
        table.setRowSelectionInterval(0, 0);

        // Delete.
        Method deleteProductMethod = ProductCatalogueGUI.class.getDeclaredMethod("deleteProduct");
        deleteProductMethod.setAccessible(true);
        deleteProductMethod.invoke(gui);

        // Verify deletion.
        assertTrue(fakeProductFunctions.isDeleted(1), "Product should be marked as deleted");
        
        // Verify table is empty.
        Field tableModelField = ProductCatalogueGUI.class.getDeclaredField("tableModel");
        tableModelField.setAccessible(true);
        DefaultTableModel model = (DefaultTableModel) tableModelField.get(gui);
        assertEquals(0, model.getRowCount(), "Table should be empty after deletion");
    }

    // A fake of ProductFunctions for testing.
    private static class FakeProductFunctions implements InterfaceProductFunctions {
        private List<Product> fakeProducts = new ArrayList<>();
        private Set<Integer> deletedProducts = new HashSet<>();

        @Override
        public List<Product> viewProducts() {
            return fakeProducts;
        }

        @Override
        public void addProduct(Product p) {
            fakeProducts.add(p);
        }

        @Override
        public void editProduct(int productId, String newName, int newStock, double newPrice) {
            for (Product p : fakeProducts) {
                if (p.productId == productId) {
                    p.productName = newName;
                    p.stock = newStock;
                    p.price = newPrice;
                }
            }
        }

        @Override
        public void deleteProduct(int productId) {
            deletedProducts.add(productId);
            fakeProducts.removeIf(p -> p.productId == productId);
        }

        public boolean isDeleted(int productId) {
            return deletedProducts.contains(productId);
        }

        public void setFakeProducts(List<Product> products) {
            fakeProducts = products;
        }
    }
}