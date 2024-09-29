// CartPreferences.java
package com.example.pmg302_project.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.pmg302_project.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CartPreferences {
    private static final String PREFS_NAME = "cart_prefs";
    private static final String CART_KEY = "cart";

    public static void saveCart(Context context, List<Product> cart) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(cart);
        editor.putString(CART_KEY, json);
        editor.apply();
    }

    public static List<Product> loadCart(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(CART_KEY, null);
        Type type = new TypeToken<ArrayList<Product>>() {}.getType();
        return json == null ? new ArrayList<>() : gson.fromJson(json, type);
    }
}