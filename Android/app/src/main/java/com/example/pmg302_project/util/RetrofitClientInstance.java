package com.example.pmg302_project.util;

import com.example.pmg302_project.Utils.COMMONSTRING;
import com.example.pmg302_project.service.CustomerService;
import com.example.pmg302_project.service.FavoriteService;
import com.example.pmg302_project.service.ProductService;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {
    static String ip = COMMONSTRING.ip;
    private static Retrofit retrofit;
    private static final String BASE_URL = "http://"+ip+":8081/api/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static FavoriteService getFavoriteService() {
        return getRetrofitInstance().create(FavoriteService.class);
    }
    public static ProductService getProductService() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit.create(ProductService.class);
    }
    public static CustomerService getCustomerService() {
        return getRetrofitInstance().create(CustomerService.class);
    }
}
