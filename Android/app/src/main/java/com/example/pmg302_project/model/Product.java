package com.example.pmg302_project.model;

import com.google.gson.annotations.SerializedName;

public class Product {
    @SerializedName("productId")
    private int id;
    @SerializedName("productName")
    private String name;
    private String description;
    private double price;
    @SerializedName("imageLink")
    private String imageLink;
    private String type;
    private int purchaseCount;
    private double rate;
    @SerializedName("stockQuantity")
    private int quantity; // Add this field
    private String size;
    private String color; // Add this field
    public Product() {}
    // Constructor
    public Product(int id, String name, String description, double price, String imageLink, String type, double rate, int purchaseCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imageLink = imageLink;
        this.type = type;
        this.rate = rate;
        this.purchaseCount = purchaseCount;
        this.quantity = quantity;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public String getImageLink() { return imageLink; }
    public double getRate() { return rate; }
    public int getPurchaseCount() { return purchaseCount; }
    public String getType() { return type; }
    public int getQuantity() { return quantity; }
    public String getSize() { return size; }
    public String getColor() { return color; }
    public void setQuantity(int quantity) { this.quantity = quantity; } // Add this setter
    public void setSize(String size) { this.size = size; }
    public void setColor(String color) { this.color = color; } // Add this setter
    public void setId(int id) { this.id=id; }
    public void setName(String name) { this.name=name; }
    public void setPrice(double price) { this.price=price; }
    public void setImageLink(String imageLink) { this.imageLink=imageLink; }
}
