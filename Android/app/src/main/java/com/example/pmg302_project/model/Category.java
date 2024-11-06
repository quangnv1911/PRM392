package com.example.pmg302_project.model;

public class Category {
    private Integer categoryId;
    private String categoryName;


    @Override
    public String toString() {
        return categoryId + " - " + categoryName; // Customize this as needed (e.g., "ID: " + id + ", Name: " + name)
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }
}
