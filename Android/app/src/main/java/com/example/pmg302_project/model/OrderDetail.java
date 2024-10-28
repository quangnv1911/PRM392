package com.example.pmg302_project.model;

import java.util.Optional;

public class OrderDetail {

    private Integer id;
    private Integer quantity;
    private Integer unitPrice;
    private Orders order;
    private Product product;
    private Optional<Coupon> coupon;
    private String size;
    private String color;

}
