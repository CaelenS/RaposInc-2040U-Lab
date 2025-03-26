import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProductFunctions implements InterfaceProductFunctions {
    private final Connection conn;
    
    public ProductFunctions(Connection conn) {
        this.conn = conn;
    }
    
    //View all products
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
    
    //Add a product
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
    
    //Edit a product
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
    
    //Delete a product
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
    
    //Search for a product based on Product Name, Genre, or Manufacturer
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
    
    public List<Product> filterProducts(String column, String operator, String filterValue) {
        List<Product> products = new ArrayList<>();
        
        // Define allowed columns and operators to avoid SQL injection.
        Set<String> allowedColumns = new HashSet<>();
        allowedColumns.add("Price");
        allowedColumns.add("Stock");
        allowedColumns.add("Rating");
        allowedColumns.add("Product_Name");
        allowedColumns.add("Genre");
        allowedColumns.add("Manufacturer");
        allowedColumns.add("UPC");
        allowedColumns.add("Description");
        
        // Validate column
        if (!allowedColumns.contains(column)) {
            System.out.println("Invalid filter column: " + column);
            return products;
        }
        
        // For numeric columns, accept only these operators.
        if (column.equals("Price") || column.equals("Stock") || column.equals("Rating")) {
            if (!operator.equals("<") && !operator.equals("=") && !operator.equals(">")) {
                System.out.println("Invalid operator for numeric column: " + operator);
                return products;
            }
        } else {
            // For non-numeric columns, we use equality check.
            operator = "=";
        }
        
        // Build the SQL query. Note: column is already validated.
        String sql = "SELECT * FROM Products WHERE " + column + " " + operator + " ?";
        
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // Set parameter according to column type.
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
}
