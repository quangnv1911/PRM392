package com.example.pmg302_project.service;

import com.example.pmg302_project.model.Category;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface CategoryApi {
    @GET("/api/categories")
    Call<List<Category>> getCategories();

    @POST("/api/categories")
    Call<Category> createCategory(@Body Category category);
    @DELETE("/api/categories/{id}")
    Call<Void> deleteCategory(@Path("id") Integer id);
}
