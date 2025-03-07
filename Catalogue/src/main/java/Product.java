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
