import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.util.Arrays;
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
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class ProductCatalogueGUI extends JFrame {
    private JFrame frame;
    private JTable table;
    private DefaultTableModel tableModel;
    //private ProductFunctions productFunctions; //BlackBoxTesting
    private InterfaceProductFunctions productFunctions;
    private User currentUser;
    // Class field for the timer:
    private Timer suggestionTimer;
    // Prevent re‐entrance from our own updates.
    private boolean suggestionIsUpdating = false;
    // Store the last query to avoid reloading if unchanged.
    private String lastSuggestionQuery = "";

    
    // Filtering/search components.
    private JComboBox<String> columnDropdown, operatorDropdown, suggestionDropdown;
    private JTextField searchField, filterValueField;
    private JButton searchButton;
    private JPanel searchPanel;
    // Class field to store current column selected for suggestions.
    private String currentSuggestionColumn = "";
    
    // Define which columns are numeric and which are categorical.
    private final String[] numericalColumns = {"Price", "Stock", "Rating"};
    // For Product_Name we use free-text search (no suggestions).
    private final String[] categoricalColumns = {"Genre", "Manufacturer"};

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
        
        setTitle("Product Catalogue");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create search and filter controls.
        searchField = new JTextField(15);
        columnDropdown = new JComboBox<>(new String[]{"Select Column", "Product_Name", "Stock", "Price", "Genre", "Rating", "Manufacturer"});
        operatorDropdown = new JComboBox<>(new String[]{"<", "=", ">"});
        suggestionDropdown = new JComboBox<>();
        filterValueField = new JTextField(10);
        searchButton = new JButton("Search");
        searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by:"));
        searchPanel.add(columnDropdown);
        searchPanel.add(operatorDropdown);
        searchPanel.add(suggestionDropdown);
        searchPanel.add(filterValueField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.NORTH);

        // Initially, hide filter controls (only show as needed)
        operatorDropdown.setVisible(false);
        suggestionDropdown.setVisible(false);
        filterValueField.setVisible(false);

        // When the selected column changes, update visible filter fields.
        columnDropdown.addActionListener(e -> updateFilterUI());
        // Wire search button
        searchButton.addActionListener(e -> searchProducts());

        // Set up table (the rest of your table view remains unchanged).
        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Stock", "Price", "Genre", "Rating", "Manufacturer", "UPC", "Description"}, 0);
        table = new JTable(tableModel);
        loadProducts();
        
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add admin buttons only if user is admin.
        if ("admin".equals(user.role)) { 
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add");
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");
            
            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            add(buttonPanel, BorderLayout.SOUTH);
            
            addButton.addActionListener(e -> addProduct());
            editButton.addActionListener(e -> editProduct());
            deleteButton.addActionListener(e -> deleteProduct());
        }
        setVisible(true);
    }
    //BlackBoxTesting
    public ProductCatalogueGUI(User user, InterfaceProductFunctions productFunctions) {
        this.currentUser = user;
        this.productFunctions = productFunctions;

        if (currentUser == null) {
            JOptionPane.showMessageDialog(null, "No user logged in.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        setTitle("Product Catalogue");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Same GUI setup as in your main constructor:
        searchField = new JTextField(15);
        columnDropdown = new JComboBox<>(new String[]{"Select Column", "Product_Name", "Stock", "Price", "Genre", "Rating", "Manufacturer"});
        operatorDropdown = new JComboBox<>(new String[]{"<", "=", ">"});
        suggestionDropdown = new JComboBox<>();
        filterValueField = new JTextField(10);
        searchButton = new JButton("Search");
        searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(new JLabel("Filter by:"));
        searchPanel.add(columnDropdown);
        searchPanel.add(operatorDropdown);
        searchPanel.add(suggestionDropdown);
        searchPanel.add(filterValueField);
        searchPanel.add(searchButton);

        add(searchPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.NORTH);

        operatorDropdown.setVisible(false);
        suggestionDropdown.setVisible(false);
        filterValueField.setVisible(false);
        columnDropdown.addActionListener(e -> updateFilterUI());
        searchButton.addActionListener(e -> searchProducts());

        tableModel = new DefaultTableModel(new String[]{"ID", "Name", "Stock", "Price", "Genre", "Rating", "Manufacturer", "UPC", "Description"}, 0);
        table = new JTable(tableModel);
        loadProducts();

        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        if ("admin".equals(user.role)) {
            JPanel buttonPanel = new JPanel();
            JButton addButton = new JButton("Add");
            JButton editButton = new JButton("Edit");
            JButton deleteButton = new JButton("Delete");

            buttonPanel.add(addButton);
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            add(buttonPanel, BorderLayout.SOUTH);

            addButton.addActionListener(e -> addProduct());
            editButton.addActionListener(e -> editProduct());
            deleteButton.addActionListener(e -> deleteProduct());
        }

        setVisible(true);
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
    
    public void searchProducts() {
        String searchText = searchField.getText().trim();
        String selectedColumn = (String) columnDropdown.getSelectedItem();
        String operator = (String) operatorDropdown.getSelectedItem();
        String selectedValue = "";
        
        // For non-numeric fields (including Product_Name), use the suggestion dropdown's text.
        if (!Arrays.asList(numericalColumns).contains(selectedColumn)) {
            JTextField editor = (JTextField) suggestionDropdown.getEditor().getEditorComponent();
            selectedValue = editor.getText().trim();
        } else {
            // For numeric columns, use the free text field.
            selectedValue = filterValueField.getText();
        }
        System.out.println("Search Text: " + searchText);
        System.out.println("Selected Column: " + selectedColumn);
        System.out.println("Operator: " + operator);
        System.out.println("Selected Value: " + selectedValue);
        // Call your searchProducts function.
        // Note: The 4th parameter 'filterValue' is unused for non-numeric filters.
        List<Product> results = ProductFunctions.searchProducts(searchText, selectedColumn, operator, "", selectedValue);
        updateTable(results);
    }
    
    // Updates the JTable with the search/filter results.
    private void updateTable(List<Product> products) {
        tableModel.setRowCount(0);
        for (Product p : products) {
            tableModel.addRow(new Object[]{
                p.productId, p.productName, p.stock, p.price, p.genre, p.rating, p.manufacturer, p.upc, p.description
            });
        }
    }
    
    private void updateFilterUI() {
        String selectedColumn = (String) columnDropdown.getSelectedItem();
        if (selectedColumn == null || selectedColumn.equals("Select Column")) {
            operatorDropdown.setVisible(false);
            suggestionDropdown.setVisible(false);
            filterValueField.setVisible(false);
            return;
        }
        boolean isNumeric = Arrays.asList(numericalColumns).contains(selectedColumn);
        
        // For numeric columns, use the operator dropdown and free-text field.
        operatorDropdown.setVisible(isNumeric);
        filterValueField.setVisible(isNumeric);
        
        // For categorical fields (including Product_Name) use the autocomplete dropdown.
        if (!isNumeric) {
            currentSuggestionColumn = selectedColumn;
            filterValueField.setVisible(false);
            
            // Remove old suggestionDropdown from its parent if present.
            if (suggestionDropdown.getParent() != null) {
                suggestionDropdown.getParent().remove(suggestionDropdown);
            }
            // Create a new AutocompleteJComboBox based on DatabaseSearchable.
            AutocompleteJComboBox autoCombo = new AutocompleteJComboBox(new DatabaseSearchable(selectedColumn, productFunctions));
            suggestionDropdown = autoCombo;
            
            // Insert the dropdown before the Search button so that the Search button remains last.
            int index = searchPanel.getComponentCount() - 1; // assuming searchButton is the last added component
            searchPanel.add(suggestionDropdown, index);
            searchPanel.revalidate();
            searchPanel.repaint();
        }
    }
    
    private void setupSuggestionDropdownListener() {
        JTextField editor = (JTextField) suggestionDropdown.getEditor().getEditorComponent();
        // Remove any existing listeners.
        for (javax.swing.event.DocumentListener dl : ((javax.swing.text.AbstractDocument) editor.getDocument()).getListeners(javax.swing.event.DocumentListener.class)) {
            editor.getDocument().removeDocumentListener(dl);
        }
        editor.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void update() {
                if (suggestionIsUpdating) {
                    return;
                }
                String text = editor.getText().trim();
                // Only update if the text has changed.
                if (text.equals(lastSuggestionQuery)) {
                    return;
                }
                lastSuggestionQuery = text;
                updateSuggestionDropdown(text);
            }
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                update();
            }
        });
    }
    
    private void updateSuggestionDropdown(String typedText) {
        // Stop any pending timer.
        if (suggestionTimer != null && suggestionTimer.isRunning()) {
            suggestionTimer.stop();
        }
        // Start a new timer with a 300ms delay.
        suggestionTimer = new Timer(300, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SwingUtilities.invokeLater(() -> {
                    suggestionIsUpdating = true;
                    try {
                        JTextField editor = (JTextField) suggestionDropdown.getEditor().getEditorComponent();
                        // Remove caret update code -- let the editor manage its caret.
                        // int docLength = editor.getDocument().getLength();
                        // int caretPos = editor.getCaretPosition();
                        // if (caretPos > docLength) {
                        //     caretPos = docLength;
                        // }
                        
                        suggestionDropdown.removeAllItems();
        
                        if (typedText.isEmpty()) {
                            suggestionDropdown.setPopupVisible(false);
                            return;
                        }
                        
                        // Retrieve suggestions from the database.
                        List<String> suggestions = productFunctions.getDistinctValues(currentSuggestionColumn, typedText);
                        for (String s : suggestions) {
                            suggestionDropdown.addItem(s);
                        }
                        suggestionDropdown.setPopupVisible(!suggestions.isEmpty());
                        
                        // Optionally, if you really need to restore caret, try a delayed restoration.
                        // SwingUtilities.invokeLater(() -> {
                        //     try {
                        //         int newDocLength = editor.getDocument().getLength();
                        //         editor.setCaretPosition(Math.min(caretPos, newDocLength));
                        //     } catch (IllegalArgumentException ex) {
                        //         editor.setCaretPosition(editor.getDocument().getLength());
                        //     }
                        // });
                        
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        suggestionIsUpdating = false;
                    }
                });
            }
        });
        suggestionTimer.setRepeats(false);
        suggestionTimer.start();
    }
}