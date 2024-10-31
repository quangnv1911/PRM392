package com.example.demo.Dtos.Request;

import com.example.demo.model.Categorie;
import jdk.jfr.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateDto {
    private String productName;
    private String image;
    private Double price;
    private Integer stockQuantity;
    private Integer categoryId;
    private String imageLink;
    private String type;
    private String description;

    public ProductCreateDto(String productName, String description, Double price, Integer stockQuantity) {
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }
}
