package com.example.pmg302_project.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.DTOs.ProductDTO;
import com.example.pmg302_project.R;
import com.example.pmg302_project.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductManageAdapter extends RecyclerView.Adapter<ProductManageAdapter.ProductViewHolder> {
    private List<ProductDTO> productList = new ArrayList<>();
    private Context context;

    public ProductManageAdapter(Context context, List<ProductDTO> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_manage, parent, false);
        return new ProductViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductDTO product = productList.get(position);
        holder.productName.setText(product.getProductName());
        holder.price.setText(String.valueOf(product.getPrice()));
        holder.stockQuantity.setText(product.getStockQuantity().toString());
        // Thêm sự kiện chỉnh sửa và xóa sản phẩm tại đây.
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productName, price, stockQuantity;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName_manage);
            price = itemView.findViewById(R.id.price_manage);
            stockQuantity = itemView.findViewById(R.id.quantity_manage);
        }
    }
}
