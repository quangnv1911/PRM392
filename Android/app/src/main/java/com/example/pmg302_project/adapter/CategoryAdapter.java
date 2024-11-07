package com.example.pmg302_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pmg302_project.model.Category;
import com.example.pmg302_project.R;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private OnCategoryItemClickListener onCategoryItemClickListener;

    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    public interface OnCategoryItemClickListener {
        void onCategoryItemClick(int position);
    }

    public void setOnCategoryItemClickListener(OnCategoryItemClickListener listener) {
        this.onCategoryItemClickListener = listener;
    }

    // Method to add a new category to the top of the list
    public void addCategory(Category category) {
        categories.add(0, category); // Add at the beginning of the list
        notifyItemInserted(0); // Notify the adapter about the new item
    }

    public void removeCategory(int position) {
        categories.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.categoryName.setText(category.getCategoryName());

        // Set the click listener for the delete icon
        holder.btnDeleteCategory.setOnClickListener(v -> {
            if (onCategoryItemClickListener != null) {
                onCategoryItemClickListener.onCategoryItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        ImageView btnDeleteCategory; // Reference to the delete button

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.categoryName);
            btnDeleteCategory = itemView.findViewById(R.id.btnDeleteCategoty); // Initialize delete button
        }
    }
}