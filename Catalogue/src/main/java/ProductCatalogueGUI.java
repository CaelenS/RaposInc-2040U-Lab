import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
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
    private ProductFunctions productFunctions;

    public ProductCatalogueGUI() {
        Connection conn = DatabaseConnection.connect();
        if (conn == null) {
            JOptionPane.showMessageDialog(null, "Database connection failed.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        productFunctions = new ProductFunctions(conn);

        frame = new JFrame("Product Catalog");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Stock", "Price", "Genre", "Rating", "Manufacturer", "UPC", "Description"}, 0);
        table = new JTable(tableModel);
        loadProducts();

        JScrollPane scrollPane = new JScrollPane(table);
        frame.add(scrollPane, BorderLayout.CENTER);

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

    public static void main(String[] args) {
        ProductCatalogueGUI gui = new ProductCatalogueGUI();
    }
}
