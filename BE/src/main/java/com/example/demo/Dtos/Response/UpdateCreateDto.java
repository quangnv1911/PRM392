package com.example.demo.Dtos.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCreateDto {
    private Integer productId;
    private String productName;
    private String image;
    private Double price;
    private Integer stockQuantity;
    private Integer categoryId;
    private String imageLink;
    private String type;
    private String description;
    private List<String> sizes;
    private List<String> colors;
    private List<String> imageProductDetails;
    private Integer PurchaseCount;
}
