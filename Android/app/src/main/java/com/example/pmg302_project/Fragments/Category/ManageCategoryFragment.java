package com.example.pmg302_project.Fragments.Category;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Category;
import com.example.pmg302_project.Utils.RetrofitClient;
import com.example.pmg302_project.service.CategoryApi;
import com.example.pmg302_project.adapter.CategoryAdapter;
import com.example.pmg302_project.util.NetworkChangeReceiver;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ManageCategoryFragment extends Fragment {

    private RecyclerView recyclerViewCategories;
    private CategoryAdapter categoryAdapter;
    private EditText editTextCategoryName;
    private NetworkChangeReceiver networkChangeReceiver;
    private List<Category> categories; // Declare categories list

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_manage_category, container, false);

        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        editTextCategoryName = view.findViewById(R.id.editTextCategoryName);
        Button buttonAddCategory = view.findViewById(R.id.buttonAddCategory);

        buttonAddCategory.setOnClickListener(v -> addCategory());

        fetchCategories();

        // Initialize and register network change receiver
        networkChangeReceiver = new NetworkChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        getContext().registerReceiver(networkChangeReceiver, filter);

        return view;
    }

    private void fetchCategories() {
        CategoryApi categoryApi = RetrofitClient.getClient().create(CategoryApi.class);
        Call<List<Category>> call = categoryApi.getCategories();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categories = response.body(); // Assign fetched categories to the member variable
                    categoryAdapter = new CategoryAdapter(categories);
                    recyclerViewCategories.setAdapter(categoryAdapter);

                    // Set the click listener for deleting categories
                    categoryAdapter.setOnCategoryItemClickListener(position -> {
                        int categoryId = categories.get(position).getCategoryId(); // Ensure categories is accessible
                        deleteCategory(categoryId, position);
                    });

                } else {
                    Toast.makeText(getContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                Log.e("ManageCategoryFragment", "Error fetching categories", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCategory() {
        String categoryName = editTextCategoryName.getText().toString().trim();
        if (categoryName.isEmpty()) {
            Toast.makeText(getContext(), "Category name cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        Category newCategory = new Category();
        newCategory.setCategoryName(categoryName);

        CategoryApi categoryApi = RetrofitClient.getClient().create(CategoryApi.class);
        Call<Category> call = categoryApi.createCategory(newCategory);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Category addedCategory = response.body();
                    Toast.makeText(getContext(), "Category added", Toast.LENGTH_SHORT).show();
                    editTextCategoryName.setText(""); // Clear input field

                    // Add the new category to the adapter and refresh the view
                    categoryAdapter.addCategory(addedCategory);
                    recyclerViewCategories.scrollToPosition(0); // Scroll to show the new item
                } else {
                    Toast.makeText(getContext(), "Failed to add category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Log.e("ManageCategoryFragment", "Error adding category", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteCategory(int categoryId, int position) {
        CategoryApi categoryApi = RetrofitClient.getClient().create(CategoryApi.class);
        Call<Void> call = categoryApi.deleteCategory(categoryId);
        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Category deleted", Toast.LENGTH_SHORT).show();
                    categoryAdapter.removeCategory(position);
                } else {
                    Toast.makeText(getContext(), "Failed to delete category", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("ManageCategoryFragment", "Error deleting category", t);
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (networkChangeReceiver != null) {
            getContext().unregisterReceiver(networkChangeReceiver);
        }
    }
}