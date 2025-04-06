import javax.swing.JTable;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

@TestMethodOrder(OrderAnnotation.class)
public class BlackBoxTesting {

    @Test
    @Order(1)
    public void testApplicationLaunch() {
        try {
            new LoginGUI(true);
            assertTrue(true, "Application launched without exceptions.");
            Thread.sleep(1000);
        } catch (Exception e) {
            fail("Application failed to launch: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testLoginToCatalogueGUI() {
        try {
            User testUser = new User(1, "Sully", "admin");
            ProductCatalogueGUI gui = new ProductCatalogueGUI(testUser, new FakeProductFunctions());

            Thread.sleep(1500);

            assertTrue(gui.isVisible(), "Catalogue GUI should be visible after login.");
        } catch (Exception e) {
            fail("Failed to launch ProductCatalogueGUI: " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testProductAdditionVisibleInTable() {
        try {
            User user = new User(1, "Sully", "admin");
            FakeProductFunctions fake = new FakeProductFunctions();
            fake.addProduct(new Product(1, "Fake Game", 5, 49.99, "Board", 4.7, "FakeCorp", "111", "Fun"));

            ProductCatalogueGUI gui = new ProductCatalogueGUI(user, fake);

            Field tableField = ProductCatalogueGUI.class.getDeclaredField("table");
            tableField.setAccessible(true);
            JTable table = (JTable) tableField.get(gui);
            table.setVisible(true);

            Thread.sleep(2000);

            assertTrue(table.getRowCount() > 0, "At least one product should be present in the table after addition.");
        } catch (Exception e) {
            fail("Product addition or table verification failed: " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void testEditProductUpdatesFakeBackend() {
        try {
            User user = new User(1, "Sully", "admin");
            FakeProductFunctions fake = new FakeProductFunctions();
            fake.addProduct(new Product(1, "Old Name", 10, 20.0, "Genre", 4.0, "Brand", "UPC", "Desc"));

            // Launch GUI
            ProductCatalogueGUI gui = new ProductCatalogueGUI(user, fake);
            Thread.sleep(1000); // Allow GUI to load

            // Edit product in fake backend
            fake.editProduct(1, "New Name", 50, 30.0);

            // Force GUI to reload products after edit
            // (since editProduct in real GUI automatically calls loadProducts, but here you simulate manually)
            Method loadProductsMethod = ProductCatalogueGUI.class.getDeclaredMethod("loadProducts");
            loadProductsMethod.setAccessible(true);
            loadProductsMethod.invoke(gui);

            Product updated = fake.viewProducts().getFirst();
            assertEquals("New Name", updated.productName);
            assertEquals(50, updated.stock);
            assertEquals(30.0, updated.price);

            Thread.sleep(2000);
        } catch (Exception e) {
            fail("Editing product failed: " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    public void testDeleteProductRemovesFromBackend() {
        try {
            FakeProductFunctions fake = new FakeProductFunctions();
            fake.addProduct(new Product(1, "To Delete", 5, 10.0, "Genre", 4.0, "Brand", "UPC", "Desc"));

            fake.deleteProduct(1);

            assertEquals(0, fake.viewProducts().size(), "Product should be deleted from backend.");
        } catch (Exception e) {
            fail("Deleting product failed: " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    public void testCloseApplicationDisposesGUI() {
        try {
            User user = new User(1, "Sully", "admin");
            ProductCatalogueGUI gui = new ProductCatalogueGUI(user, new FakeProductFunctions());

            Thread.sleep(1000);
            gui.dispose();

            assertFalse(gui.isVisible(), "GUI should not be visible after dispose.");
        } catch (Exception e) {
            fail("Disposing GUI failed: " + e.getMessage());
        }
    }

    // -- FAKE BACKEND CLASS --
    private static class FakeProductFunctions implements InterfaceProductFunctions {
        private final List<Product> fakeProducts = new ArrayList<>();

        @Override
        public List<String> getDistinctValues(String column, String typeFilter) {
            return List.of("FakeValue1", "FakeValue2"); //Returns fake values for testing
        }

        @Override
        public List<Product> viewProducts() {
            return new ArrayList<>(fakeProducts);
        }

        @Override
        public void addProduct(Product product) {
            fakeProducts.add(product);
        }

        @Override
        public void editProduct(int productId, String newName, int newStock, double newPrice) {
            for (Product p : fakeProducts) {
                if (p.productId == productId) {
                    p.productName = newName;
                    p.stock = newStock;
                    p.price = newPrice;
                    return;
                }
            }
        }

        @Override
        public void deleteProduct(int productId) {
            fakeProducts.removeIf(p -> p.productId == productId);
        }
    }
}
