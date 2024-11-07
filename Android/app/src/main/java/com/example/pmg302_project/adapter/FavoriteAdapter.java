package com.example.pmg302_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Product;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {

    private List<Product> productList;

    public FavoriteAdapter(List<Product> productList) {
        this.productList = productList;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.productName.setText(product.getName());
        holder.productPrice.setText(String.valueOf(product.getPrice()));
        holder.productDescription.setText(product.getDescription());
        // Assuming you have a method to load images
        holder.productImage.setImageResource(R.drawable.ic_launcher_background); // Replace with actual image loading logic

        holder.addToCartButton.setOnClickListener(v -> {
            // Handle add to cart action
        });

        holder.detailButton.setOnClickListener(v -> {
            // Handle view details action
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        TextView productName, productPrice, productDescription;
        ImageView productImage;
        Button addToCartButton, detailButton;

        FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            productDescription = itemView.findViewById(R.id.productDescription);
            productImage = itemView.findViewById(R.id.productImage);
            addToCartButton = itemView.findViewById(R.id.button);
            detailButton = itemView.findViewById(R.id.detailButton);
        }
    }
}
