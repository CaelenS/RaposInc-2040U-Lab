/**
 * The class which contains all data related to a product
 */
public class Product {
    /**
     * The unique identifier for the product.
     */
    public int productId;

    /**
     * The name of the product.
     */
    public String productName;

    /**
     * The quantity of the product available in stock.
     */
    public int stock;

    /**
     * The price of the product.
     */
    public double price;

    /**
     * The genre or category of the product (e.g., electronics, clothing, etc.).
     */
    public String genre;

    /**
     * The average rating of the product (e.g., out of 5 stars).
     */
    public double rating;

    /**
     * The manufacturer or brand of the product.
     */
    public String manufacturer;

    /**
     * The Universal Product Code (UPC) for the product.
     */
    public String upc;

    /**
     * A brief description of the product, highlighting key features or specifications.
     */
    public String description;

    /**
     * Constructs a new {@code Product} instance with the specified attributes.
     *
     * @param productId The unique identifier for the product.
     * @param productName The name of the product.
     * @param stock The available stock quantity.
     * @param price The price of the product.
     * @param genre The genre or category of the product.
     * @param rating The customer rating of the product.
     * @param manufacturer The manufacturer of the product.
     * @param upc The Universal Product Code (UPC) of the product.
     * @param description A textual description of the product.
     */
    public Product(int productId, String productName, int stock, double price, String genre, double rating, String manufacturer, String upc, String description) {
        this.productId = productId;
        this.productName = productName;
        this.stock = stock;
        this.price = price;
        this.genre = genre;
        this.rating = rating;
        this.manufacturer = manufacturer;
        this.upc = upc;
        this.description = description;
    }
}
