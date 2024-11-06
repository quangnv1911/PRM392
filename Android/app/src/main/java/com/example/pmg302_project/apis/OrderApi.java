package com.example.pmg302_project.apis;

import com.example.pmg302_project.model.Coupon;
import com.example.pmg302_project.model.OrderDetail;
import com.example.pmg302_project.model.Orders;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface OrderApi {
    @GET("api/orders/all")
    Call<List<Orders>> getAllOrders();

    @POST("api/coupons")
    Call<Orders> createOrder(@Body Orders order);

    @PUT("api/orders/update-status/{id}")
    Call<Orders> updateOrder(@Path("id") Integer id, @Body Integer statusId);

    @DELETE("api/coupons/{id}")
    Call<Void> deleteOrder(@Path("id") Integer id);

    @GET("api/order-detail/{id}")
    Call<List<OrderDetail>> getOrderDetails(@Path("id") Integer id);

}
