package com.example.demo.Dtos.Response;

import com.example.demo.model.Categorie;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ListProductDto {
    private Integer productId;

  
    private String productName;

  
    private String image;

    private double price;

    private Integer stockQuantity;
    
    private Integer categoryId;

    private String imageLink;
    private String type;
    private Integer purchaseCount;
    private double rate;
    private String description;
    private Integer createdBy;

    private List<String> sizes;

    private List<String> colors;

    private List<String> imageProductDetails;
}
