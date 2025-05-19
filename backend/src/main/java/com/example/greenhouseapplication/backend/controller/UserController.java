package com.example.greenhouseapplication.backend.controller;

import java.util.Collections;
import java.util.Collections;
import java.util.List;
import java.util.Optional;


import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.greenhouseapplication.backend.model.User;
import com.example.greenhouseapplication.backend.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;




@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;


    @GetMapping
    public ResponseEntity<List<User>> getAllUsers()
    {
        return new ResponseEntity<>(userService.AllUser(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<User>> getUser(@PathVariable("id") ObjectId Id) {
        return new ResponseEntity<>(userService.oneUser(Id),HttpStatus.OK);
    }


    @GetMapping("/name/{username}")
    public ResponseEntity<Optional<User>> getUserByUsername(@PathVariable("username") String username) {
        return new ResponseEntity<>(userService.UserByUsername(username), HttpStatus.OK);
    }
    
    

    @PostMapping
    public ResponseEntity<User> addAUser(@RequestBody User user) {
        if (userService.existsByUsername(user.getUsername())) {
            return new ResponseEntity<>(user,HttpStatus.CONFLICT);
        }
        User newUser = userService.AddUser(user);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
   

    @DeleteMapping("/name/{username}")
    public ResponseEntity<String> deleteUserByUsername(@PathVariable("username") String username) {
        userService.deleteUserByUsername(username);
        return new ResponseEntity<>("Successfully Deleted The Account : "+username,HttpStatus.OK);
    }

    @PutMapping("/update-username")
    public ResponseEntity<User> updateUsername(@RequestParam String currentUsername, @RequestParam String newUsername) {
        Optional<User> updatedUser = userService.updateUsername(currentUsername, newUsername);
        return updatedUser
            .map(user -> new ResponseEntity<>(user, HttpStatus.OK))
            .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/update-password")
    public ResponseEntity<?> updatePassword(
            @RequestParam String username,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

            Optional<User> updatedUser = userService.updatePassword(username, oldPassword, newPassword);

            if (updatedUser.isPresent()) {
                return new ResponseEntity<>(updatedUser.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>("Invalid username or old password", HttpStatus.UNAUTHORIZED);
            }
    }
    @PutMapping("/update-role")
    public ResponseEntity<User> updateRole(
            @RequestParam String username,
            @RequestParam String newRole) {

        Optional<User> updated = userService.updateRole(username, newRole);
        return updated
                .map(u -> new ResponseEntity<>(u, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }



    
}
