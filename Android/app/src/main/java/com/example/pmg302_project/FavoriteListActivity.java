package com.example.pmg302_project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.adapter.FavoriteAdapter;
import com.example.pmg302_project.model.Account;
import com.example.pmg302_project.util.FavoriteResponse;
import com.example.pmg302_project.model.Product;
import com.example.pmg302_project.service.FavoriteService;
import com.example.pmg302_project.util.RetrofitClientInstance;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoriteListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFavorite;
    private FavoriteAdapter favoriteAdapter;
    private FavoriteService favoriteService;
    private Long userId;
    private int accountId;
    private Set<Integer> favoriteProductIds;
    private List<Product> favoriteProducts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list);

        // Initialize RecyclerView
        recyclerViewFavorite = findViewById(R.id.recyclerViewFavorite);
        recyclerViewFavorite.setLayoutManager(new LinearLayoutManager(this));

        // Initialize favoriteService
        favoriteService = RetrofitClientInstance.getFavoriteService();

        // Retrieve userId from InMemoryStorage and convert it to Long
        String userIdString = (String) InMemoryStorage.get("userId");

        if (userIdString != null) {
            try {
                this.userId = Long.valueOf(userIdString);
                getAccountByUserId(this.userId, this::onAccountIdRetrieved);
            } catch (NumberFormatException e) {
                Log.e("FavoriteListActivity", "Invalid user ID format: " + userIdString, e);
                this.userId = null;
                this.accountId = -1;
            }
        } else {
            Log.e("FavoriteListActivity", "User ID is null! Cannot fetch account ID.");
            this.userId = null;
            this.accountId = -1;
        }
    }

    private void getAccountByUserId(Long userId, AccountIdCallback callback) {
        Call<Account> call = favoriteService.getAccountByUserId(userId);
        call.enqueue(new Callback<Account>() {
            @Override
            public void onResponse(Call<Account> call, Response<Account> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Account account = response.body();
                    callback.onAccountIdRetrieved(account.getId());
                } else {
                    Log.d("FavoriteListActivity", "Failed to retrieve account. Response code: " + response.code());
                    callback.onAccountIdRetrieved(-1);
                }
            }

            @Override
            public void onFailure(Call<Account> call, Throwable t) {
                Log.e("FavoriteListActivity", "Error fetching account: " + t.getMessage());
                callback.onAccountIdRetrieved(-1);
            }
        });
    }

    private void onAccountIdRetrieved(int accountId) {
        this.accountId = accountId;
        if (accountId != -1) {
            fetchFavoriteProductIds(accountId);
        } else {
            Log.e("FavoriteListActivity", "Failed to retrieve account ID");
            Toast.makeText(this, "Failed to retrieve account ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchFavoriteProductIds(int accountId) {
        Call<FavoriteResponse> call = favoriteService.getFavoriteProductIds(accountId);
        call.enqueue(new Callback<FavoriteResponse>() {
            @Override
            public void onResponse(Call<FavoriteResponse> call, Response<FavoriteResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    favoriteProductIds = new HashSet<>(response.body().getFavoriteProductIds());
                    fetchFavoriteProducts();
                } else {
                    Log.e("FavoriteListActivity", "Failed to fetch favorite product IDs");
                    Toast.makeText(FavoriteListActivity.this, "Failed to fetch favorite product IDs", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<FavoriteResponse> call, Throwable t) {
                Log.e("FavoriteListActivity", "Error fetching favorite product IDs: " + t.getMessage());
            }
        });
    }

    private void fetchFavoriteProducts() {
        if (favoriteProductIds != null && !favoriteProductIds.isEmpty()) {
            favoriteProducts = new ArrayList<>();
            List<Call<Product>> calls = new ArrayList<>();
            final int[] completedRequests = {0}; // Use this to track completed requests

            for (Integer productId : favoriteProductIds) {
                Call<Product> call = favoriteService.getProductById(productId);
                calls.add(call);
                call.enqueue(new Callback<Product>() {
                    @Override
                    public void onResponse(Call<Product> call, Response<Product> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            favoriteProducts.add(response.body());
                        } else {
                            Log.e("FavoriteListActivity", "Failed to fetch product with ID: " + productId);
                        }

                        completedRequests[0]++; // Increment counter when request completes

                        // If all requests are completed, update the adapter
                        if (completedRequests[0] == favoriteProductIds.size()) {
                            favoriteAdapter = new FavoriteAdapter(favoriteProducts);
                            recyclerViewFavorite.setAdapter(favoriteAdapter); // Update the adapter once all data is fetched
                        }
                    }

                    @Override
                    public void onFailure(Call<Product> call, Throwable t) {
                        Log.e("FavoriteListActivity", "Error fetching product with ID: " + productId + " - " + t.getMessage());
                        completedRequests[0]++; // Increment counter in case of failure

                        // If all requests have completed (success or failure), update the adapter
                        if (completedRequests[0] == favoriteProductIds.size()) {
                            favoriteAdapter = new FavoriteAdapter(favoriteProducts);
                            recyclerViewFavorite.setAdapter(favoriteAdapter); // Update the adapter once all data is fetched
                        }
                    }
                });
            }
        } else {
            Log.e("FavoriteListActivity", "No favorite products found");
            Toast.makeText(this, "No favorite products found", Toast.LENGTH_SHORT).show();
        }
    }

    interface AccountIdCallback {
        void onAccountIdRetrieved(int accountId);
    }
}
