package com.example.demo.controller;

import com.example.demo.model.Favorite;
import com.example.demo.repo.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @PostMapping
    public ResponseEntity<?> addFavorite(
            @RequestParam Integer accountId,
            @RequestParam Integer productId) {

        // Check if the favorite item already exists
        Optional<Favorite> existingFavorite = favoriteRepository.findByAccountIdAndProductId(accountId, productId);
        if (existingFavorite.isPresent()) {
            return ResponseEntity.badRequest().body("Favorite item already exists for this account and product.");
        }

        // Create new Favorite object if not already present
        Favorite favoriteItem = new Favorite();
        favoriteItem.setAccountId(accountId);
        favoriteItem.setProductId(productId);
        favoriteItem.setCreatedAt(LocalDateTime.now()); // Set created date

        // Save the favorite item to the repository
        Favorite savedFavoriteItem = favoriteRepository.save(favoriteItem);
        return ResponseEntity.ok(savedFavoriteItem);
    }

    @GetMapping
    public ResponseEntity<List<Favorite>> getFavorites(@RequestParam Integer accountId) {
        // Fetch all favorite items for a specific account
        List<Favorite> favorites = favoriteRepository.findByAccountId(accountId);
        return ResponseEntity.ok(favorites);
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteFavorite(
            @RequestParam Integer accountId,
            @RequestParam Integer productId) {

        // Check if the favorite item exists before deleting
        if (favoriteRepository.findByAccountIdAndProductId(accountId, productId).isEmpty()) {
            return ResponseEntity.notFound().build(); // Return 404 if not found
        }

        // Delete the favorite item
        favoriteRepository.deleteByAccountIdAndProductId(accountId, productId);
        return ResponseEntity.noContent().build(); // Return 204 No Content
    }
}
