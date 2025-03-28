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

public class ProductFunctions implements InterfaceProductFunctions {
    private final Connection conn;
    
    public ProductFunctions(Connection conn) {
        this.conn = conn;
    }
    
    // View all products
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
    
    // Add a product
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
    
    // Edit a product
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
    
    // Delete a product
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
    
    // Search for a product (free-text search on Product_Name, Genre, or Manufacturer)
    public List<Product> searchProducts(String searchTerm) {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM Products WHERE Product_Name ILIKE ? OR Genre ILIKE ? OR Manufacturer ILIKE ?";
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
    
    // Filter products based on a given column, operator, and filter value.
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
    
    // Get suggestions from the database (returns at most 7 distinct matches)
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

    // This static searchProducts method integrates free-text search and filtering.
    // For numeric filters, operator and filterValue are used;
    // for categorical filters, the selected value from the drop-down is used.
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
