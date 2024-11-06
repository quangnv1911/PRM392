package com.example.pmg302_project.apis;

import com.example.pmg302_project.DTOs.UserDTO;
import com.example.pmg302_project.model.Coupon;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface UserApi {
    @GET("api/users")
    Call<List<UserDTO>> getAllUsers();
}
