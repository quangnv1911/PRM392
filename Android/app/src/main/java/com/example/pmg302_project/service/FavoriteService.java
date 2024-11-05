package com.example.pmg302_project.service;

import com.example.pmg302_project.model.Account;

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
    @GET("api/account")
    Call<Account> getAccountByUserName(@Query("username") String username);
}
