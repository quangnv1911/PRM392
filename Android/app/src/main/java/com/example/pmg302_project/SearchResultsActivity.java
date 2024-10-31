// SearchResultsActivity.java
package com.example.pmg302_project;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.adapter.ProductAdapter;
import com.example.pmg302_project.model.Product;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerViewSearchResults;
    private TextView noResultsMessage;
    private ProductAdapter productAdapter;
    private List<Product> productList = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        Toolbar toolbar = findViewById(R.id.toolbar_homepage);
        setSupportActionBar(toolbar);
        recyclerViewSearchResults = findViewById(R.id.recyclerViewSearchResults);
        noResultsMessage = findViewById(R.id.noResultsMessage);

        recyclerViewSearchResults.setLayoutManager(new LinearLayoutManager(this));
        productAdapter = new ProductAdapter(this, productList, null, false);
        recyclerViewSearchResults.setAdapter(productAdapter);

        String searchResults = getIntent().getStringExtra("searchResults");
        if (searchResults != null) {
            try {
                JSONArray jsonArray = new JSONArray(searchResults);
                if (jsonArray.length() == 0) {
                    noResultsMessage.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        Product product = new Product(
                                jsonObject.getInt("productId"),
                                jsonObject.getString("productName"),
                                jsonObject.getString("description"),
                                jsonObject.getDouble("price"),
                                jsonObject.getString("imageLink"),
                                jsonObject.getString("type"),
                                jsonObject.getDouble("rate"),
                                jsonObject.getInt("purchaseCount")
                        );
                        productList.add(product);
                    }
                    productAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            noResultsMessage.setVisibility(View.VISIBLE);
        }
    }
}