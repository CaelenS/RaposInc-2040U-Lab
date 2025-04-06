import javax.swing.JTable;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Order;

@TestMethodOrder(OrderAnnotation.class)
public class TransparentBoxTesting {

    @Test
    @Order(1)
    public void testApplicationLaunch() {
        try {
            LoginGUI.main(new String[]{});
            assertTrue(true, "Application launched without exceptions.");
            Thread.sleep(1000);
        } catch (Exception e) {
            fail("Application failed to launch: " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testEmployeeLoginToCatalogueGUI() {
        try {
            User testUser = new User(1, "Caelen", "employee");
            ProductCatalogueGUI gui = new ProductCatalogueGUI(testUser, new FakeProductFunctionsTwo());

            Thread.sleep(1500);

            assertTrue(gui.isVisible(), "Catalogue GUI should be visible to user");
            assertTrue(testUser.role == "employee","User's role should be employee");
        } catch (Exception e) {
            fail("Failed to login as an Employee " + e.getMessage());
        }
    }

    @Test
    @Order(3)
    public void testAdminLoginToCatalogueGUI() {
        try {
            User testUser = new User(1, "Caelen", "admin");
            ProductCatalogueGUI gui = new ProductCatalogueGUI(testUser, new FakeProductFunctionsTwo());

            Thread.sleep(1500);

            assertTrue(gui.isVisible(), "Catalogue GUI should be visible to user");
            assertTrue(testUser.role == "admin","User's role should be admin");
        } catch (Exception e) {
            fail("Failed to login as an Admin " + e.getMessage());
        }
    }

    @Test
    @Order(4)
    public void testCatalogueGUIIntegration() {
        try {
            User testUser = new User(1, "Caelen", "admin");
            FakeProductFunctionsTwo fake = new FakeProductFunctionsTwo();

            fake.addProduct(new Product(1, "Fake Game", 5, 49.99, "Board", 4.7, "FakeCorp", "111", "Fun"));

            ProductCatalogueGUI gui = new ProductCatalogueGUI(testUser, fake);

            Field tableField = ProductCatalogueGUI.class.getDeclaredField("table");
            tableField.setAccessible(true);
            JTable table = (JTable) tableField.get(gui);
            table.setVisible(true);

            Thread.sleep(1500);



            assertTrue(gui.isVisible(), "Catalogue GUI should be visible to user");
            assertTrue(table.getRowCount() > 0,"User can see products");
        } catch (Exception e) {
            fail("Failed to login as an Admin " + e.getMessage());
        }
    }

    private static class FakeProductFunctionsTwo implements InterfaceProductFunctions {
        private final List<Product> fakeProducts = new ArrayList<>();

        @Override
        public List<String> getDistinctValues(String column, String typeFilter) {
            return List.of("FakeValue1", "FakeValue2");
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
