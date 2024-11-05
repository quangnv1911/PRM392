package com.example.pmg302_project.apis;

import com.example.pmg302_project.model.Coupon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface CouponApi {
    @GET("api/coupons")
    Call<List<Coupon>> getAllCoupons();

    @POST("api/coupons")
    Call<Coupon> createCoupon(@Body Coupon coupon);

    @PUT("api/coupons/{id}")
    Call<Coupon> updateCoupon(@Path("id") Integer id, @Body Coupon coupon);

    @DELETE("api/coupons/{id}")
    Call<Void> deleteCoupon(@Path("id") Integer id);
}
