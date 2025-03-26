import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

public class ProductCatalogueGUI {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    private InterfaceProductFunctions productFunctions;
    private User currentUser;
    
    public ProductCatalogueGUI(User user) {
        // Get the current user from Session rather than from a parameter.
        this.currentUser = user;
        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Connection conn = DatabaseConnection.connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        productFunctions = new ProductFunctions(conn);
        
        frame = new JFrame("Product Catalog");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        
        // Create a composite top panel to hold both search and filter controls.
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());
        
        // --------------------
        // Search Panel (remains unchanged)
        JPanel searchPanel = new JPanel();
        JTextField searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        // Wire search button as before.
        searchButton.addActionListener(e -> {
            String searchTerm = searchField.getText().trim();
            searchProducts(searchTerm);
        });
        
        // --------------------
        // Filter Panel (new)
        JPanel filterPanel = new JPanel();
        
        // Create a drop down for selecting the column to filter.
        String[] columns = {"Product_Name", "Price", "Stock", "Rating", "Genre", "Manufacturer"};
        JComboBox<String> columnSelector = new JComboBox<>(columns);
        filterPanel.add(new JLabel("Filter by:"));
        filterPanel.add(columnSelector);
        
        // Create a drop down for numeric operators.
        String[] numericOps = {"<", "=", ">"};
        JComboBox<String> operatorSelector = new JComboBox<>(numericOps);
        filterPanel.add(operatorSelector);
        
        // Create a text field for numeric filter value.
        JTextField filterValueField = new JTextField(10);
        filterPanel.add(filterValueField);
        
        // Create a combo box for categorical suggestions.
        // Initially hidden until a categorical column is selected.
        JComboBox<String> suggestionComboBox = new JComboBox<>();
        suggestionComboBox.setVisible(false);
        filterPanel.add(suggestionComboBox);
        
        // Listen to changes in the column selector.
        columnSelector.addActionListener(e -> {
            String selectedColumn = (String) columnSelector.getSelectedItem();
            // Define which columns are numeric.
            boolean isNumeric = selectedColumn.equals("Price") || 
            selectedColumn.equals("Stock") || 
            selectedColumn.equals("Rating");
            
            if (isNumeric) {
                // For numeric columns:
                // Show the operator dropdown and numeric text field.
                operatorSelector.setVisible(true);
                filterValueField.setVisible(true);
                // Optionally, reset any locked operator by user.
                operatorSelector.setSelectedIndex(0); 
                // Hide the suggestion dropdown.
                suggestionComboBox.setVisible(false);
            } else {
                // For categorical columns:
                // Either lock operator to "=" (operator dropdown no longer matters)
                // Hide the operator dropdown and numeric text field.
                operatorSelector.setVisible(false);
                filterValueField.setVisible(false);
                // And display the suggestion combo box.
                String[] suggestions = getSuggestionsFor(selectedColumn);
                suggestionComboBox.removeAllItems();
                for (String s : suggestions) {
                    suggestionComboBox.addItem(s);
                }
                suggestionComboBox.setVisible(true);
            }
        });
        
        // Create an "Apply Filter" button.
        JButton applyFilterButton = new JButton("Apply Filter");
        filterPanel.add(applyFilterButton);
        
        // Wire the filter apply button.
        applyFilterButton.addActionListener(e -> {
            String selectedColumn = (String) columnSelector.getSelectedItem();
            String operator = "="; // For categorical filters the operator is locked.
            String filterValue = "";
            boolean isNumeric = selectedColumn.equals("Price") ||
            selectedColumn.equals("Stock") ||
            selectedColumn.equals("Rating");
            
            if (isNumeric) {
                operator = (String) operatorSelector.getSelectedItem();
                filterValue = filterValueField.getText().trim();
            } else {
                // Use the suggestion combo box value for categorical filters.
                filterValue = (String) suggestionComboBox.getSelectedItem();
            }
            
            List<Product> filteredProducts = ((ProductFunctions) productFunctions)
            .filterProducts(selectedColumn, operator, filterValue);
            
            tableModel.setRowCount(0);  // Clear the table.
            for (Product p : filteredProducts) {
                tableModel.addRow(new Object[]{
                    p.productId, p.productName, p.stock, p.price, p.genre,
                    p.rating, p.manufacturer, p.upc, p.description
                });
            }
        });
        
        // --------------------
        // Add search and filter panels to the composite top panel.
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(filterPanel, BorderLayout.SOUTH);
        
        // Add the composite top panel to the frame.
        frame.add(topPanel, BorderLayout.NORTH);
        
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Stock", "Price", "Genre", "Rating", "Manufacturer", "UPC", "Description"}, 0);
        table = new JTable(tableModel);
        loadProducts();
        
        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Add admin buttons only if user is admin.
        if ("admin".equals(user.role)) { 
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add");
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");
            
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            frame.add(buttonPanel, BorderLayout.SOUTH);
            
            addButton.addActionListener(e -> addProduct());
            editButton.addActionListener(e -> editProduct());
            deleteButton.addActionListener(e -> deleteProduct());
        }
        frame.setVisible(true);
    }
    
    private void loadProducts() {
        tableModel.setRowCount(0);
        List<Product> products = productFunctions.viewProducts();
        for (Product product : products) {
            tableModel.addRow(new Object[]{product.productId, product.productName, product.stock, product.price, product.genre, product.rating, product.manufacturer, product.upc, product.description});
        }
    }
    
    private void addProduct() {
        JTextField nameField = new JTextField();
        JTextField stockField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField genreField = new JTextField();
        JTextField ratingField = new JTextField();
        JTextField manufacturerField = new JTextField();
        JTextField upcField = new JTextField();
        JTextField descriptionField = new JTextField();
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreField);
        panel.add(new JLabel("Rating:"));
        panel.add(ratingField);
        panel.add(new JLabel("Manufacturer:"));
        panel.add(manufacturerField);
        panel.add(new JLabel("UPC:"));
        panel.add(upcField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        try {
            String name = nameField.getText().trim();
            int stock = Integer.parseInt(stockField.getText().trim());
            double price = Double.parseDouble(priceField.getText().trim());
            String genre = genreField.getText().trim();
            double rating = Double.parseDouble(ratingField.getText().trim());
            String manufacturer = manufacturerField.getText().trim();
            String upc = upcField.getText().trim();
            String description = descriptionField.getText().trim();
            
            Product product = new Product(0, name, stock, price, genre, rating, manufacturer, upc, description);
            productFunctions.addProduct(product);
            loadProducts();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage());
        }
    }
    
    private void editProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a product to edit.");
            return;
        }
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        
        JTextField nameField = new JTextField(tableModel.getValueAt(selectedRow, 1).toString());
        JTextField stockField = new JTextField(tableModel.getValueAt(selectedRow, 2).toString());
        JTextField priceField = new JTextField(tableModel.getValueAt(selectedRow, 3).toString());
        JTextField genreField = new JTextField(tableModel.getValueAt(selectedRow, 4).toString());
        JTextField ratingField = new JTextField(tableModel.getValueAt(selectedRow, 5).toString());
        JTextField manufacturerField = new JTextField(tableModel.getValueAt(selectedRow, 6).toString());
        JTextField upcField = new JTextField(tableModel.getValueAt(selectedRow, 7).toString());
        JTextField descriptionField = new JTextField(tableModel.getValueAt(selectedRow, 8).toString());
        
        JPanel panel = new JPanel(new GridLayout(0, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Stock:"));
        panel.add(stockField);
        panel.add(new JLabel("Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Genre:"));
        panel.add(genreField);
        panel.add(new JLabel("Rating:"));
        panel.add(ratingField);
        panel.add(new JLabel("Manufacturer:"));
        panel.add(manufacturerField);
        panel.add(new JLabel("UPC:"));
        panel.add(upcField);
        panel.add(new JLabel("Description:"));
        panel.add(descriptionField);
        
        int result = JOptionPane.showConfirmDialog(frame, panel, "Edit Product", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) {
            return;
        }
        
        try {
            String newName = nameField.getText().trim();
            int newStock = Integer.parseInt(stockField.getText().trim());
            double newPrice = Double.parseDouble(priceField.getText().trim());
            String newGenre = genreField.getText().trim();
            double newRating = Double.parseDouble(ratingField.getText().trim());
            String newManufacturer = manufacturerField.getText().trim();
            String newUpc = upcField.getText().trim();
            String newDescription = descriptionField.getText().trim();
            
            // Create updated product with new values.
            Product updatedProduct = new Product(productId, newName, newStock, newPrice, newGenre, newRating, newManufacturer, newUpc, newDescription);
            productFunctions.editProduct(productId, newName, newStock, newPrice);
            
            loadProducts();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Invalid input: " + ex.getMessage());
        }
    }
    
    private void deleteProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a product to delete.");
            return;
        }
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        productFunctions.deleteProduct(productId);
        loadProducts();
    }
    
    public void searchProducts(String searchTerm) {
        List<Product> results = ((ProductFunctions)productFunctions).searchProducts(searchTerm);
        tableModel.setRowCount(0); // Clear old table content
        
        for (Product p : results) {
            tableModel.addRow(new Object[]{
                p.productId, p.productName, p.stock, p.price, p.genre,
                p.rating, p.manufacturer, p.upc, p.description
            });
        }
    }
    
    // This method should query the database or use a pre-fetched list of distinct values.
    private String[] getSuggestionsFor(String column) {
        // For a production system, execute a SQL query such as:
        // SELECT DISTINCT column FROM Products ORDER BY column;
        // Here we simply return example suggestions.
        switch (column) {
            case "Product_Name":
            return new String[]{"Toy Car", "Doll", "Puzzle", "Board Game"};
            case "Genre":
            return new String[]{"Toy", "Puzzle", "Board Game", "Action Figure", "Video Games"};
            case "Manufacturer":
            return new String[]{"Brand A", "Brand B", "Brand C"};
            default:
            return new String[]{};
        }
    }
}
