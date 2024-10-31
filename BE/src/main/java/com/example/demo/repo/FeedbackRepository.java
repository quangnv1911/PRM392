// src/main/java/com/example/demo/repo/FeedbackRepository.java
package com.example.demo.repo;

import com.example.demo.model.Feedback;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {
    Optional<Feedback> findByUserAndProduct(User user, Product product);

    List<Feedback> findByProductProductId(Integer productId);
}