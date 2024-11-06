package com.example.pmg302_project.apis;

import com.example.pmg302_project.model.Category;
import com.example.pmg302_project.model.Coupon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface CateogryApi {
    @GET("api/category")
    Call<List<Category>> getAllCategory();
}
