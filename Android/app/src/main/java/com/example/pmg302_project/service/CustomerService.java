package com.example.pmg302_project.service;

import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.model.Orders;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface CustomerService {
    @GET("/api/customers")
    Call<List<Account>> getCustomers();
    @GET("customers/{id}/orders")
    Call<Map<String, Object>> getCustomerOrdersWithTotal(@Path("id") int accountId);
}
