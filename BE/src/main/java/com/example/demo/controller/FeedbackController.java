// src/main/java/com/example/demo/controller/FeedbackController.java
package com.example.demo.controller;

import com.example.demo.model.Feedback;
import com.example.demo.model.Product;
import com.example.demo.model.User;
import com.example.demo.repo.FeedbackRepository;
import com.example.demo.repo.ProductRepository;
import com.example.demo.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class FeedbackController {
    @Autowired
    private FeedbackRepository feedbackRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/submitFeedback")
    public ResponseEntity<String> submitFeedback(@RequestBody Map<String, String> feedbackDetails) {
        String username = feedbackDetails.get("username");
        int productId = Integer.parseInt(feedbackDetails.get("productId"));
        String feedbackText = feedbackDetails.get("feedback");

        Optional<User> user = userRepository.findByUsername(username);
        Optional<Product> product = productRepository.findById((long) productId);

        if (user.isPresent() && product.isPresent()) {
            Optional<Feedback> existingFeedback = feedbackRepository.findByUserAndProduct(user.get(), product.get());
            Feedback feedback;
            if (existingFeedback.isPresent()) {
                feedback = existingFeedback.get();
                feedback.setFeedbackText(feedbackText);
            } else {
                feedback = new Feedback();
                feedback.setUser(user.get());
                feedback.setProduct(product.get());
                feedback.setFeedbackText(feedbackText);
            }
            feedbackRepository.save(feedback);
            return ResponseEntity.ok("Feedback submitted successfully");
        }
        return ResponseEntity.status(404).body("User or Product not found");
    }

    @GetMapping("/getFeedbacks")
    public ResponseEntity<Map<String, String>> getFeedbacks(@RequestParam Integer productId) {
        List<Feedback> feedbacks = feedbackRepository.findByProductProductId(productId);
        Map<String, String> feedbackDetails = feedbacks.stream()
                .collect(Collectors.toMap(
                        feedback -> feedback.getUser().getFullName(),
                        Feedback::getFeedbackText
                ));
        return ResponseEntity.ok(feedbackDetails);
    }
}