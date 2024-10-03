package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "Account")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer accountId;

    @Column(nullable = false)
    private String fullname;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String image;

    @Column(nullable = false)
    private LocalDate createdOn;

    private Integer createdBy;
    private Integer modifiedBy;
    private LocalDate modifiedOn;

    @ManyToOne
    @JoinColumn(name = "roleId", nullable = false)
    private Role role;
}
