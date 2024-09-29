package com.example.demo.service;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.repo.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

import com.example.demo.model.User;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Optional<User> login(String username, String password, String loginMethod, String fullName) {
        if (loginMethod.equals("google")) {
            Optional<User> user = userRepository.findByUsername(username);
            if (user.isEmpty()) {
                User newUser = new User();
                newUser.setUsername(username);
                newUser.setPassword("");
                newUser.setFullName(fullName);
                userRepository.save(newUser);
                return Optional.of(newUser);
            }
            return user;
        } else {
            return userRepository.findByUsernameAndPassword(username, password);
        }
    }

    public Optional<User> register(String username, String fullName) {
        Optional<com.example.demo.model.User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            com.example.demo.model.User existingUser = user.get();
            existingUser.setFullName(fullName);
            userRepository.save(existingUser);
            return Optional.of(existingUser);
        }
        return Optional.empty();
    }

    public boolean resetPassword(String email) {
        Optional<User> user = userRepository.findByUsername(email);
        if (user.isPresent()) {
            String newPassword = generateResetToken(user.get());
            // Send email with the reset link
            emailService.sendEmail(email, "Reset Password", "Your new password is: " + newPassword);

            return true;
        }
        return false;
    }

    private String generateResetToken(User user) {
        // Implement token generation logic
        // Generate a new random password
        String newPassword = RandomStringUtils.randomAlphanumeric(10);

        // Update the user's password in the database
        user.setPassword(newPassword);
        userRepository.save(user);

        // Return the new password
        return newPassword;    }
}
