package com.example.demo.repo;


import com.example.demo.model.Categorie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Categorie, Integer> {
        Optional<Categorie> findByCategoryName(String categoryName);
}
