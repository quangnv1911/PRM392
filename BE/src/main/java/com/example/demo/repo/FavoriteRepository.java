package com.example.demo.repo;

import com.example.demo.model.Favorite;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> { // Change Integer to Long
    List<Favorite> findByAccountId(Integer accountId);
    Optional<Favorite> findByAccountIdAndProductId(Integer accountId, Integer productId);
    @Transactional
    void deleteByAccountIdAndProductId(Integer accountId, Integer productId);

}
