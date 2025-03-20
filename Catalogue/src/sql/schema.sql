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

CREATE TABLE IF NOT EXISTS Users (
    User_ID SERIAL PRIMARY KEY,
    Username VARCHAR(50) UNIQUE NOT NULL,
    Password VARCHAR(255) NOT NULL,
    Role VARCHAR(10) NOT NULL CHECK (Role IN ('admin', 'employee'))
);
