package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "type")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class ProductType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String text;
    private String type;

}
