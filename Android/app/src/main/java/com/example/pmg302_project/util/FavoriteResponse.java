package com.example.pmg302_project.util;

import java.util.List;
import java.util.Set;

public class FavoriteResponse {
    // Assuming the favorite product IDs are stored in a List<Integer>
    private List<Integer> favoriteProductIds;

    // Getter method for the list of favorite product IDs
    public List<Integer> getFavoriteProductIds() {
        return favoriteProductIds;
    }

    // Optionally, a setter if needed
    public void setFavoriteProductIds(List<Integer> favoriteProductIds) {
        this.favoriteProductIds = favoriteProductIds;
    }
}


