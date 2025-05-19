package com.example.greenhouseapplication.backend.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.greenhouseapplication.backend.model.User;
import com.example.greenhouseapplication.backend.repository.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<User> AllUser() {
        return userRepository.findAll();
    }

    public Optional<User> oneUser(ObjectId Id){
        return userRepository.findById(Id);
    } 

    
    public Optional<User> UserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public User AddUser(User user)
    {
        return userRepository.save(user);
    }

    public void deleteUserByUsername(String username) {
        userRepository.deleteByUsername(username); 
    }

    public Optional<User> updateUsername(String currentUsername, String newUsername) {
        Optional<User> userOptional = userRepository.findByUsername(currentUsername);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(newUsername);
            userRepository.save(user);
            return Optional.of(user);
        }
        return Optional.empty();
    }

    public Optional<User> updatePassword(String username, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Optional<User> updateRole(String username, String newRole) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isPresent()) {
            User u = opt.get();
            u.setRole(newRole);
            userRepository.save(u);
            return Optional.of(u);
        }
        return Optional.empty();
    }
    
    
    
    
}
