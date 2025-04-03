import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Handles product-related operations including viewing, adding, editing,
 * deleting, searching, filtering, and retrieving product suggestions.
 */
public class ProductFunctions implements InterfaceProductFunctions {
    private final Connection conn;
    
    /**
     * Constructs a ProductFunctions instance with a given database connection.
     * @param conn The database connection.
     */
    public ProductFunctions(Connection conn) {
        this.conn = conn;
    }
    
    /**
     * Retrieves all products from the database.
     * @return A list of all products.
     */
    public List<Product> viewProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("Product_ID"),
                    rs.getString("Product_Name"),
                    rs.getInt("Stock"),
                    rs.getDouble("Price"),
                    rs.getString("Genre"),
                    rs.getDouble("Rating"),
                    rs.getString("Manufacturer"),
                    rs.getString("UPC"),
                    rs.getString("Description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("FAILURE: Error Viewing Products: " + e.getMessage());
        }
        return products;
    }
    
    /**
     * Adds a new product to the database.
     * @param product The product to add.
     */
    public void addProduct(Product product) {
        String sql = "INSERT INTO Products (Product_Name, Stock, Price, Genre, Rating, Manufacturer, UPC, Description) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, product.productName);
            pstmt.setInt(2, product.stock);
            pstmt.setDouble(3, product.price);
            pstmt.setString(4, product.genre);
            pstmt.setDouble(5, product.rating);
            pstmt.setString(6, product.manufacturer);
            pstmt.setString(7, product.upc);
            pstmt.setString(8, product.description);
            pstmt.executeUpdate();
            System.out.println("SUCCESS: Product Added.");
        } catch (SQLException e) {
            System.out.println("FAILURE: Error Adding Product: " + e.getMessage());
        }
    }
    
    /**
     * Updates an existing product's details.
     * @param productId The ID of the product to update.
     * @param newName The new name of the product.
     * @param newStock The new stock value.
     * @param newPrice The new price.
     */
    public void editProduct(int productId, String newName, int newStock, double newPrice) {
        String sql = "UPDATE Products SET Product_Name = ?, Stock = ?, Price = ? WHERE Product_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newStock);
            pstmt.setDouble(3, newPrice);
            pstmt.setInt(4, productId);
            pstmt.executeUpdate();
            System.out.println("SUCCESS: Product Updated.");
        } catch (SQLException e) {
            System.out.println("FAILURE: Error Updating Product: " + e.getMessage());
        }
    }
    
    /**
     * Deletes a product from the database.
     * @param productId The ID of the product to delete.
     */
    public void deleteProduct(int productId) {
        String sql = "DELETE FROM Products WHERE Product_ID = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
            System.out.println("SUCCESS: Product Deleted.");
        } catch (SQLException e) {
            System.out.println("FAILURE: Error Deleting Product: " + e.getMessage());
        }
    }
    
    /**
     * Searches for products based on a search term.
     * @param searchTerm The search term used for matching product name, genre, or manufacturer.
     * @return A list of matching products.
     */
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE Product_Name ILIKE ? OR Genre ILIKE ? OR Manufacturer ILIKE ? OR UPC ILIKE ? OR Description ILIKE ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            pstmt.setString(1, likeTerm);
            pstmt.setString(2, likeTerm);
            pstmt.setString(3, likeTerm);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("Product_ID"),
                    rs.getString("Product_Name"),
                    rs.getInt("Stock"),
                    rs.getDouble("Price"),
                    rs.getString("Genre"),
                    rs.getDouble("Rating"),
                    rs.getString("Manufacturer"),
                    rs.getString("UPC"),
                    rs.getString("Description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("FAILURE: Error Searching Products: " + e.getMessage());
        }
        return products;
    }
    
    /**
     * Filters products based on a specified column, operator, and filter value.
     * 
     * @param column The column to filter by.
     * @param operator The comparison operator (e.g., <, =, >).
     * @param filterValue The value to filter against.
     * @return A list of filtered Product objects.
     */
    public List<Product> filterProducts(String column, String operator, String filterValue) {
        List<Product> products = new ArrayList<>();
        // Allowed columns:
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("Price", "Stock", "Rating", "Product_Name", "Genre", "Manufacturer", "UPC", "Description"));
        if (!allowedColumns.contains(column)) {
            System.out.println("Invalid filter column: " + column);
            return products;
        }
        
        // For numeric columns, enforce valid operators.
        if (column.equals("Price") || column.equals("Stock") || column.equals("Rating")) {
            if (!operator.equals("<") && !operator.equals("=") && !operator.equals(">")) {
                System.out.println("Invalid operator for numeric column: " + operator);
                return products;
            }
        } else {
            operator = "=";
        }
        
        String sql = "SELECT * FROM Products WHERE " + column + " " + operator + " ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            if (column.equals("Price") || column.equals("Rating")) {
                pstmt.setDouble(1, Double.parseDouble(filterValue));
            } else if (column.equals("Stock")) {
                pstmt.setInt(1, Integer.parseInt(filterValue));
            } else {
                pstmt.setString(1, filterValue);
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                products.add(new Product(
                    rs.getInt("Product_ID"),
                    rs.getString("Product_Name"),
                    rs.getInt("Stock"),
                    rs.getDouble("Price"),
                    rs.getString("Genre"),
                    rs.getDouble("Rating"),
                    rs.getString("Manufacturer"),
                    rs.getString("UPC"),
                    rs.getString("Description")
                ));
            }
        } catch (SQLException e) {
            System.out.println("FAILURE: Error Filtering Products: " + e.getMessage());
        }
        return products;
    }
    
    /**
     * Retrieves a list of distinct suggestions for a specified column based on a partial input.
     *
     * @param column The column to search for suggestions (must be Product_Name, Genre, or Manufacturer).
     * @param partialValue The partial input to match against the column values.
     * @return A list of up to 7 distinct suggestions matching the input.
     */
    public List<String> getSuggestions(String column, String partialValue) {
        List<String> suggestions = new ArrayList<>();
        // Allow suggestions only for these columns.
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("Product_Name", "Genre", "Manufacturer"));
        if (!allowedColumns.contains(column)) {
            System.out.println("Suggestions not supported for column: " + column);
            return suggestions;
        }
        String sql = "SELECT DISTINCT " + column + " FROM Products WHERE " + column + " ILIKE ? ORDER BY " + column + " LIMIT 7";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + partialValue + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                suggestions.add(rs.getString(column));
            }
        } catch (SQLException e) {
            System.out.println("FAILURE: Error fetching suggestions: " + e.getMessage());
        }
        return suggestions;
    }

    /**
     * Retrieves a list of distinct values for a given column that match a search term.
     * Only specific columns (Product_Name, Genre, Manufacturer) are allowed for search.
     *
     * @param column     The column name to search within.
     * @param searchTerm The term to match against the column values.
     * @return A list of distinct matching values, limited to 7 results.
     */
    public List<String> getDistinctValues(String column, String searchTerm) {
        List<String> values = new ArrayList<>();
        
        // Ensure only specific columns can be searched for suggestions.
        Set<String> allowedColumns = new HashSet<>(Arrays.asList("Product_Name", "Genre", "Manufacturer"));
        if (!allowedColumns.contains(column)) {
            System.out.println("Invalid column for suggestions: " + column);
            return values;
        }
    
        String sql = "SELECT DISTINCT " + column + " FROM Products WHERE " + column + " ILIKE ? ORDER BY " + column + " LIMIT 7";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                values.add(rs.getString(column));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching distinct values: " + e.getMessage());
        }
        
        return values;
    }

    /**
     * Searches for products based on a free-text search and/or filtering criteria.
     * Supports filtering by numeric values using operators and categorical values using exact matches.
     *
     * @param searchText     The free-text search term for product name, genre, or manufacturer.
     * @param selectedColumn The column to filter by (e.g., Price, Stock, Rating, Genre, etc.).
     * @param operator       The operator for numeric filtering (e.g., >, <, =).
     * @param filterValue    The value for numeric filtering.
     * @param selectedValue  The value for categorical filtering.
     * @return A list of products matching the search and filter criteria.
     */
    public static List<Product> searchProducts(String searchText, String selectedColumn, String operator, String filterValue, String selectedValue) {
        List<Product> productList = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT * FROM Products WHERE 1=1");
        boolean hasSearchText = searchText != null && !searchText.isEmpty();
        boolean hasFilter = selectedColumn != null && !selectedColumn.equals("Select Column");
        
        if (hasSearchText) {
            query.append(" AND (Product_Name LIKE ? OR Genre LIKE ? OR Manufacturer LIKE ?)");
        }
        if (hasFilter) {
            if (Arrays.asList("Price", "Stock", "Rating").contains(selectedColumn)) {
                query.append(" AND ").append(selectedColumn).append(" ").append(operator).append(" ?");
            } else {
                query.append(" AND ").append(selectedColumn).append(" = ?");
            }
        }
        
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            int paramIndex = 1;
            if (hasSearchText) {
                String likeTerm = "%" + searchText + "%";
                stmt.setString(paramIndex++, likeTerm);
                stmt.setString(paramIndex++, likeTerm);
                stmt.setString(paramIndex++, likeTerm);
            }
            if (hasFilter) {
                if (Arrays.asList("Price", "Stock", "Rating").contains(selectedColumn)) {
                    stmt.setDouble(paramIndex++, Double.parseDouble(selectedValue));
                } else {
                    stmt.setString(paramIndex++, selectedValue);
                }
            }
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("Product_ID"),
                    rs.getString("Product_Name"),
                    rs.getInt("Stock"),
                    rs.getDouble("Price"),
                    rs.getString("Genre"),
                    rs.getDouble("Rating"),
                    rs.getString("Manufacturer"),
                    rs.getString("UPC"),
                    rs.getString("Description")
                );
                productList.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error searching products: " + e.getMessage());
        }
        return productList;
    }
}
