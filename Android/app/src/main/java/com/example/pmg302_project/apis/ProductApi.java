package com.example.pmg302_project.apis;

import com.example.pmg302_project.DTOs.ProductDTO;
import com.example.pmg302_project.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ProductApi {
    @GET("api/products")
    Call<List<ProductDTO>> getProducts();

    @POST("api/product")
    Call<ProductDTO> addProduct(@Body ProductDTO product);

    @PUT("api/product/{id}")
    Call<ProductDTO> updateProduct(@Path("id") Integer id, @Body ProductDTO product);

    @DELETE("api/product/{id}")
    Call<Void> deleteProduct(@Path("id") Integer id);

    @GET("api/product/{id}")
    Call<ProductDTO> getProduct(@Path("id") Integer id);
}
