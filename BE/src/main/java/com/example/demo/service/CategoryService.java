package com.example.demo.service;

import com.example.demo.model.Categorie;
import com.example.demo.repo.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Categorie> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Categorie getCategoryById(Integer id) {
        return categoryRepository.findById(id).orElse(null);
    }

    public Categorie createCategory(Categorie category) {
        return categoryRepository.save(category);
    }

    public Categorie updateCategory(Integer id, Categorie category) {
        if (categoryRepository.existsById(id)) {
            category.setCategoryId(id);
            return categoryRepository.save(category);
        }
        return null; // or throw an exception
    }
    public boolean categoryExistsByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName).isPresent();
    }

    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}