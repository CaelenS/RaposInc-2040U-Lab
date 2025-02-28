import java.awt.BorderLayout;
import java.sql.Connection;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
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
        String name = JOptionPane.showInputDialog("Enter product name:");
        int stock = Integer.parseInt(JOptionPane.showInputDialog("Enter stock:"));
        double price = Double.parseDouble(JOptionPane.showInputDialog("Enter price:"));
        String genre = JOptionPane.showInputDialog("Enter genre:");
        double rating = Double.parseDouble(JOptionPane.showInputDialog("Enter rating:"));
        String manufacturer = JOptionPane.showInputDialog("Enter manufacturer:");
        String upc = JOptionPane.showInputDialog("Enter UPC:");
        String description = JOptionPane.showInputDialog("Enter description:");
        
        Product product = new Product(0, name, stock, price, genre, rating, manufacturer, upc, description);
        productFunctions.addProduct(product);
        loadProducts();
    }

    private void editProduct() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(frame, "Please select a product to edit.");
            return;
        }
        int productId = (int) tableModel.getValueAt(selectedRow, 0);
        String newName = JOptionPane.showInputDialog("Enter new name:", tableModel.getValueAt(selectedRow, 1));
        int newStock = Integer.parseInt(JOptionPane.showInputDialog("Enter new stock:", tableModel.getValueAt(selectedRow, 2).toString()));
        double newPrice = Double.parseDouble(JOptionPane.showInputDialog("Enter new price:", tableModel.getValueAt(selectedRow, 3).toString()));

        productFunctions.editProduct(productId, newName, newStock, newPrice);
        loadProducts();
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
