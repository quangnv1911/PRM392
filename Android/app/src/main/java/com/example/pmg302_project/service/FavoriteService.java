package com.example.pmg302_project.service;

import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.model.Product;

import java.util.Set;
import  com.example.pmg302_project.util.FavoriteResponse;
import retrofit2.http.Path;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
public interface FavoriteService {
    @POST("favorites")
    @FormUrlEncoded
    Call<Void> addFavorite(
            @Field("accountId") Integer accountId,
            @Field("productId") Integer productId
    );
    @DELETE("favorites")
    Call<Void> removeFavorite(@Query("accountId") int accountId, @Query("productId") int productId);
    @GET("account/by-userid/{userId}")
    Call<Account> getAccountByUserId(@Path("userId") Long userId);
    @GET("account/{username}")
    Call<Account> getAccountByUsername(@Path("username") String username);
    @GET("favorites/{accountId}")
    Call<FavoriteResponse> getFavoriteProductIds(@Path("accountId") int accountId);
    @GET("products/{id}") // Adjust the endpoint according to your API
    Call<Product> getProductById(@Path("id") Integer id);

}
