package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flipper")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Flipper {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageLink;
}
