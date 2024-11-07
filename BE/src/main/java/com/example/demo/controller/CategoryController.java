package com.example.demo.controller;

import com.example.demo.model.Categorie;
import com.example.demo.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    public List<Categorie> getAllCategories() {
        return categoryService.getAllCategories();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Categorie> getCategoryById(@PathVariable Integer id) {
        Categorie category = categoryService.getCategoryById(id);
        return category != null ? ResponseEntity.ok(category) : ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody Categorie category) {
        // Check if a category with the same name already exists
        if (categoryService.categoryExistsByName(category.getCategoryName())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Category with this name already exists.");
        }

        // Create and return the new category if no duplicate found
        Categorie createdCategory = categoryService.createCategory(category);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCategory);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Categorie> updateCategory(@PathVariable Integer id, @RequestBody Categorie category) {
        Categorie updatedCategory = categoryService.updateCategory(id, category);
        return updatedCategory != null ? ResponseEntity.ok(updatedCategory) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}