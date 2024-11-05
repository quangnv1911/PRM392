// src/main/java/com/example/demo/model/Feedback.java
package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "feedback")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String feedbackText;
}