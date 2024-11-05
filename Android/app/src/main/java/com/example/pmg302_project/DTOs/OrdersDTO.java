package com.example.pmg302_project.DTOs;

import com.example.pmg302_project.model.OrderDetail;

import java.util.Date;
import java.util.List;

public class OrdersDTO {
    private Integer orderId;
    private Date orderDate;
    private Integer totalQuantity;
    private Double totalPrice;
    private Integer status;
    private List<OrderDetail> orderDetails;
}
