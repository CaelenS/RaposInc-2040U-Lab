import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the product-related functions.
 */
class ProductFunctionsTest {
    private FakeProductFunctions fakeProductFunctions;

    /**
     * Sets up the test environment before each test.
     */
    @BeforeEach
    void setUp() {
        fakeProductFunctions = new FakeProductFunctions();
    }

    /**
     * Tests adding a product to the fake product list.
     */
    @Test
    void testAddProduct() {
        Product p = new Product(1, "Test Product", 10, 99.99, "Electronics", 4.5, "TestCorp", "123456789", "A great product");
        fakeProductFunctions.addProduct(p);

        assertEquals(1, fakeProductFunctions.viewProducts().size());
    }

    /**
     * Tests editing a product's details in the fake product list.
     */
    @Test
    void testEditProduct() {
        Product p = new Product(1, "Test Product", 10, 99.99, "Electronics", 4.5, "TestCorp", "123456789", "A great product");
        fakeProductFunctions.addProduct(p);

        fakeProductFunctions.editProduct(1, "Updated Name", 20, 89.99);
        assertEquals("Updated Name", fakeProductFunctions.viewProducts().get(0).productName);
    }

    /**
     * Tests deleting a product from the fake product list.
     */
    @Test
    void testDeleteProduct() {
        Product p = new Product(1, "Test Product", 10, 99.99, "Electronics", 4.5, "TestCorp", "123456789", "A great product");
        fakeProductFunctions.addProduct(p);

        fakeProductFunctions.deleteProduct(1);
        assertEquals(0, fakeProductFunctions.viewProducts().size());
    }


    /**
     * A fake implementation of product functions for testing purposes.
     * Simulates product operations without database interactions.
     */    private static class FakeProductFunctions implements InterfaceProductFunctions {
        private final List<Product> fakeProducts = new ArrayList<>();

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

        @Override
        public List<String> getDistinctValues(String column, String typeFilter) {
            return List.of("FakeValue1", "FakeValue2");  // Return fake values for testing
        }
    }

}

