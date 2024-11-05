package com.example.pmg302_project.service;

import com.example.pmg302_project.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ProductService {
    @GET("search")
    Call<List<Product>> searchProducts(@Query("productName") String productName);
}
