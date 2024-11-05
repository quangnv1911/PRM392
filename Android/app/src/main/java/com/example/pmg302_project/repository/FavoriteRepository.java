package com.example.pmg302_project.repository;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.annotation.NonNull;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.service.FavoriteService;
import com.example.pmg302_project.util.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteRepository {
    private FavoriteService favoriteService;
    private Context context;

    public FavoriteRepository(Context context) {
        this.context = context;
        favoriteService = RetrofitClientInstance.getRetrofitInstance().create(FavoriteService.class);
        if (favoriteService == null) {
            throw new NullPointerException("FavoriteService is not initialized properly.");
        }
    }


    public void addFavorite(int accountId, int productId, ImageView imgFavorite, Context context) {
        // Call the API
        Call<Void> call = favoriteService.addFavorite(accountId, productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Change the icon to indicate the item is favorited
                    imgFavorite.setImageResource(R.drawable.ic_heart_filled);
                    Toast.makeText(context, "Added to favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to add to favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void removeFavorite(int accountId, int productId, ImageView imgFavorite, Context context) {
        // Call the API
        Call<Void> call = favoriteService.removeFavorite(accountId, productId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Change the icon to indicate the item is not favorited
                    imgFavorite.setImageResource(R.drawable.ic_heart);
                    Toast.makeText(context, "Removed from favorites", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Failed to remove from favorites", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void getAccountByUsername(String username, AccountIdCallback callback) {
        favoriteService.getAccountByUserName(username).enqueue(new Callback<Account>() {
            @Override
            public void onResponse(@NonNull Call<Account> call, @NonNull Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onAccountIdRetrieved(response.body().getId());
                } else {
                    Log.d("FavoriteRepository", "Failed to retrieve account.");
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.e("FavoriteRepository", "Error fetching account: " + t.getMessage());
            }
        });
    }

    // Callback interface for accountId retrieval
    public interface AccountIdCallback {
        void onAccountIdRetrieved(int accountId);
    }
}
