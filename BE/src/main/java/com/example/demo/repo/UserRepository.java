package com.example.demo.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>  {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndPassword(String username, String password);

}
