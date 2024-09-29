package com.example.demo.repo;

import com.example.demo.model.Flipper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FlipperRepository extends JpaRepository<Flipper, Long> {
}
