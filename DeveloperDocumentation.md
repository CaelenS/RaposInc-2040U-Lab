# Developer Documentation

## Overview

This program is a Java Swing application for managing a toy product catalogue. It allows users to view, search, filter, add, edit, and delete products from a database. The application utilizes auto‑complete functionality for filtering categorical fields (such as Product Name, Genre, and Manufacturer) and separates product operations via interfaces and helper classes. Unit tests are provided to ensure reliability.

---

## Table of Contents

- [ProductCatalogueGUI.java](#productcatalogueguijava)
- [AutocompleteJComboBox.java](#autocompletejcomboboxjava)
- [ProductFunctions.java](#productfunctionsjava)
- [InterfaceProductFunctions.java](#interfaceproductfunctionsjava)
- [DatabaseConnection.java](#databaseconnectionjava)
- [DatabaseSearch.java](#databasesearchjava)
- [Product.java](#productjava)
- [Searchable.java](#searchablejava)
- [User.java](#userjava)
- [UserAuth.java](#userauthjava)
- [LoginGUI.java](#loginguijava)
- [pom.xml](#pomxml)
- [Tests/ and SQL/ Folders](#tests--and-sql-folders)
- [CatalogueGUITest.java](#catalogueguitestjava)
- [README.md](#readmemd)
- [General Suggestions & Refactoring](#general-suggestions--refactoring)

---

## ProductCatalogueGUI.java

**Location:**  
`Catalogue/src/main/java/ProductCatalogueGUI.java`

### Purpose

Provides the main graphical user interface (GUI) for the product catalogue. It displays products in a table format and includes controls for user searches, filtering, adding, editing, and deleting products.

### Key Variables

- **frame**  
  The main application window (`JFrame`) for dialogs and messages.

- **table**  
  The `JTable` that displays the list of products.

- **tableModel**  
  The `DefaultTableModel` managing product data.

- **productFunctions**  
  An instance of `ProductFunctions` to perform product‑related database operations.

- **currentUser**  
  The logged‑in user.

- **suggestionTimer**  
  A `Timer` used to debounce auto‑complete suggestions.

- **suggestionIsUpdating**  
  A boolean flag indicating if suggestions are being updated.

- **lastSuggestionQuery**  
  Caches the last auto‑complete query to avoid redundant refreshes.

- **columnDropdown, operatorDropdown, suggestionDropdown**  
  Drop‑down components for filtering:
  - `columnDropdown`: Selects the column to filter on.
  - `operatorDropdown`: Displays operator choices (e.g., `<`, `>`, `=`) for numeric filtering.
  - `suggestionDropdown`: Provides auto‑complete suggestions for categorical filters.

- **searchField, filterValueField**  
  Text fields for the main search term and numeric filter value, respectively.

- **searchButton**  
  Triggers the search action.

- **searchPanel**  
  A panel grouping search and filter controls. The Search button is always the last element.

- **currentSuggestionColumn**  
  Represents the column on which auto‑complete suggestions are based.

- **numericalColumns**  
  Array of columns (`{"Price", "Stock", "Rating"}`) that use numeric filtering.

- **categoricalColumns**  
  Array of columns (such as `{"Genre", "Manufacturer"}`) that use auto‑complete filtering.

### Methods

- **Constructor `public ProductCatalogueGUI(User user)`**  
  Initializes the GUI, sets up controls and event listeners, establishes the database connection, loads products via `loadProducts()`, and adds admin controls for add/edit/delete as needed.

- **`private void loadProducts()`**  
  Retrieves products with `productFunctions.viewProducts()` and updates the table.

- **`private void addProduct()`**  
  Displays a dialog to enter new product details, creates a `Product` object, calls `productFunctions.addProduct(product)`, then reloads products.

- **`private void editProduct()`**  
  Opens a dialog pre‑populated with the selected product's data, updates the product via `productFunctions.editProduct(...)`, and refreshes the table.

- **`private void deleteProduct()`**  
  Deletes the selected product using `productFunctions.deleteProduct(productId)` and reloads the product list.

- **`public void searchProducts()`**  
  Gathers search criteria from `searchField`, `columnDropdown`, and either `suggestionDropdown` or `filterValueField` (depending on the column type). Calls `ProductFunctions.searchProducts(...)` and updates the table with the results.

- **`private void updateTable(List<Product> products)`**  
  Clears the table model and adds each product as a new row.

- **`private void updateFilterUI()`**  
  Adjusts filter UI controls based on the selected column. For numeric columns, displays `operatorDropdown` and `filterValueField`; for categorical columns, creates a new `AutocompleteJComboBox` (using a `DatabaseSearchable` instance) inserted before the Search button.

- **`private void setupSuggestionDropdownListener()`**  
  Sets up the document listener for autocompletion on the dropdown’s editor.

- **`private void updateSuggestionDropdown(String typedText)`**  
  Uses a debounced timer (300ms) to update suggestions by calling `productFunctions.getDistinctValues(currentSuggestionColumn, typedText)`, then repopulates the dropdown while preserving user input.

---

## AutocompleteJComboBox.java

**Location:**  
`Catalogue/src/main/java/AutocompleteJComboBox.java`

### Purpose

Extends `JComboBox<String>` to provide auto‑complete functionality. It listens for text input on its editor component and updates suggestions dynamically using a searchable data source.

### Key Variables

- **searchable**  
  A `Searchable<String, String>` implementation used to retrieve suggestions.

### Methods

- **Constructor `public AutocompleteJComboBox(Searchable<String, String> searchable)`**  
  - Sets the combo box as editable.
  - Retrieves the editor (`JTextComponent`) and verifies its type.
  - Sets up a debounce mechanism with a delay (300ms) via a `Timer`.
  - Registers a `DocumentListener` that schedules suggestion updates using an internal helper (`scheduleUpdate()`) and calls `updateSuggestions()`.

- **Private Helper: `updateSuggestions()`**  
  - Captures the current text and checks against a cached value.
  - Retrieves suggestions from `searchable.search(currentText)`.
  - Clears and repopulates the combo box while ensuring the current text and caret position are preserved.
  - Keeps the editor in focus.

---

## ProductFunctions.java

**Location:**  
`Catalogue/src/main/java/ProductFunctions.java`

### Purpose

Handles product-related operations such as viewing, adding, editing, deleting, searching, and filtering products. Also provides methods to fetch suggestions and distinct values from the database.

### Key Variables

- **conn**  
  The database connection used for executing SQL queries.

### Methods

- **`public ProductFunctions(Connection conn)`**  
  Constructor that initializes the database connection.

- **`public List<Product> viewProducts()`**  
  Retrieves all products from the database.

- **`public void addProduct(Product product)`**  
  Inserts a new product into the database.

- **`public void editProduct(int productId, String newName, int newStock, double newPrice)`**  
  Updates product details identified by `productId`.

- **`public void deleteProduct(int productId)`**  
  Deletes a product from the database.

- **`public List<Product> searchProducts(String searchTerm)`**  
  Searches products based on a term (e.g., product name, genre).

- **`public List<Product> filterProducts(String column, String operator, String filterValue)`**  
  Retrieves products filtered by a specified column, operator, and value.

- **`public List<String> getSuggestions(String column, String partialValue)`**  
  Executes a SQL query with a LIKE condition (`"%partialValue%"`) to fetch suggestions.

- **`public List<String> getDistinctValues(String column, String searchTerm)`**  
  Returns distinct matching values for a column based on the search term.

- **`public static List<Product> searchProducts(String searchText, String selectedColumn, String operator, String filterValue, String selectedValue)`**  
  Combines search and filter criteria to build and execute a SQL query, returning matching products.

---

## InterfaceProductFunctions.java

**Location:**  
`Catalogue/src/main/java/InterfaceProductFunctions.java`

### Purpose

Defines the contract for product operations such as viewing, adding, editing, and deleting products. This interface enables dependency injection and facilitates testing.

### Methods (Abstract)

- **`List<Product> viewProducts();`**
- **`void addProduct(Product product);`**
- **`void editProduct(int productId, String newName, int newStock, double newPrice);`**
- **`void deleteProduct(int productId);`**

---

## DatabaseConnection.java

**Location:**  
`Catalogue/src/main/java/DatabaseConnection.java`

### Purpose

Manages the creation and handling of the database connection. It includes methods to:
- Establish a connection to the database.
- Close the connection when no longer needed.

### Key Methods

- **`public static Connection connect()`**  
  Returns a valid `Connection` object or throws an exception if the connection fails.

- **`public static void close(Connection conn)`**  
  Closes the provided database connection safely.

---

## DatabaseSearch.java

**Location:**  
`Catalogue/src/main/java/DatabaseSearch.java`

### Purpose

Provides methods for executing complex search queries against the database. This file typically includes logic for:
- Building dynamic SQL queries based on search parameters.
- Executing the query and returning results as lists of products or suggestions.

---

## Product.java

**Location:**  
`Catalogue/src/main/java/Product.java`

### Purpose

Represents a product in the catalogue. This class contains:
- Product attributes (e.g., productId, productName, stock, price, genre, rating, manufacturer, upc, description).
- Getters and setters for each attribute.
- A constructor for creating new product instances.
- An overridden `toString()` method for debug-friendly representation.

---

## Searchable.java

**Location:**  
`Catalogue/src/main/java/Searchable.java`

### Purpose

Defines a generic interface for performing searches on a data source. It includes one method:

- **`Collection<E> search(V value);`**  
  Searches for items matching the given value and returns a collection of results.

Implementations of this interface (such as for database queries) provide the logic to return suggestions or search results.

---

## User.java

**Location:**  
`Catalogue/src/main/java/User.java`

### Purpose

Represents a user of the application. Key attributes include:
- Username
- Role (admin or regular user)
- Other relevant user details

This class also includes standard getters, setters, and a constructor.

---

## UserAuth.java

**Location:**  
`Catalogue/src/main/java/UserAuth.java`

### Purpose

Handles user authentication. It provides methods to:
- Validate user credentials against the database.
- Manage sessions or tokens as necessary.
- Return a `User` object upon successful login.

---

## LoginGUI.java

**Location:**  
`Catalogue/src/main/java/LoginGUI.java`

### Purpose

Creates the login user interface. It:
- Presents fields for username and password.
- Validates credentials using `UserAuth`.
- On successful login, displays a loading screen before transitioning to `ProductCatalogueGUI`.

### Key Methods

- **Constructor `public LoginGUI()`**  
  Initializes the login form and connects to the database.

- **`private void placeComponents(JPanel panel, UserAuth auth)`**  
  Lays out labels, text fields, and buttons, and attaches an ActionListener to the login button.

- **`private void showLoadingScreen(User user)`**  
  Displays a temporary loading screen before launching the main GUI.

- **`public static void main(String[] args)`**  
  Entry point that starts the login interface.

---

## pom.xml

**Location:**  
`/RaposInc-2040U-Lab/pom.xml`

### Purpose

The Maven project file that manages dependencies, build configurations, and plugins for the application. Key sections include:
- **Dependencies:** Include libraries for Java Swing, JDBC drivers, unit testing frameworks (JUnit, etc.), and any other third‑party tools.
- **Build Plugins:** Define build steps, such as compiling the code and packaging the application.

---

## Tests/ and SQL/ Folders

### Tests/ Folder

**Location:**  
`Catalogue/src/test/java/`

### Purpose

Contains unit and integration tests for the application. These tests ensure that:
- The GUI components behave as expected.
- Database operations (via `ProductFunctions` and `InterfaceProductFunctions`) work correctly with both fake and real connections.
- Critical methods (like `loadProducts()`, `deleteProduct()`, etc.) produce the expected outcomes.

### SQL/ Folder

**Location:**  
`Catalogue/sql/`

### Purpose

Contains SQL scripts for:
- Database schema creation.
- Inserting initial test data.
- Any stored procedures or triggers used by the application.
- This folder helps developers quickly set up or reset the database for testing and development purposes.

---

## README.md

**Location:**  
`/RaposInc-2040U-Lab/README.md`

### Overview

Describes the purpose and usage of the Catalogue application. It includes:
- Instructions on how to run the application.
- Features of the application (adding, editing, deleting, and searching products).
- Screenshots and detailed usage guidelines.
- Test execution instructions and additional developer notes.
- Vide Demonstration

---

## How to Run the Program

Follow these steps to set up and run the application:

1. **Prerequisites:**
    - Ensure you have Java (JDK 8 or higher) and Maven installed.
    - Set up your database and ensure it is accessible.

2. **Environment Setup:**
    - Create an `.env` file with the required environment variables (e.g., database connection key, API keys).
    - Place the `.env` file in the `resources` folder of the project so that it can be loaded at runtime.

3. **Build the Project:**
    - Open a terminal in the project root directory.
    - Run `mvn clean install` to compile the source code and package the application.

4. **Run the Application:**
    - After a successful build, navigate to the target directory.
    - Execute the application using `java -jar RaposInc-2040U-Lab.jar` (replace with the actual jar file name if different).
    - The login screen should appear. Enter valid credentials to proceed.

5. **Additional Notes:**
    - Verify that the `resources` folder is included in your classpath.
    - If you encounter issues connecting to the database, review the configuration in the `.env` file and ensure your database service is running.


# Conclusion

This documentation provides a detailed overview of each file, class, method, and key variable in the Catalogue program. It covers both application logic and supporting files (such as database connection, user management, and Maven configurations). Use this guide to understand the architecture, identify refactoring opportunities, and ensure consistent development practices throughout the codebase.