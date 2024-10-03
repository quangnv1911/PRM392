package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
@Entity
@Table(name = "Role")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer roleId;

    @Column(nullable = false)
    private String roleName;

    @OneToMany(mappedBy = "role")
    private List<Account> accounts;
}
