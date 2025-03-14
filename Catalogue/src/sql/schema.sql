CREATE TABLE IF NOT EXISTS Products (
    Product_ID SERIAL PRIMARY KEY,
    Product_Name VARCHAR(255) NOT NULL,
    Stock INT NOT NULL,
    Price DECIMAL(10,2) NOT NULL,
    Genre VARCHAR(100),
    Rating DECIMAL(2,1),
    Manufacturer VARCHAR(255),
    UPC VARCHAR(12) UNIQUE,
    Description TEXT
);