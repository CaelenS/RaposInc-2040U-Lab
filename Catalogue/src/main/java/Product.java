public class Product {
    public int productId;
    public String productName;
    public int stock;
    public double price;
    public String genre;
    public double rating;
    public String manufacturer;
    public String upc;
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
