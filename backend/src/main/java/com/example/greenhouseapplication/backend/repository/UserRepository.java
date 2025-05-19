package com.example.greenhouseapplication.backend.repository;

import org.springframework.stereotype.Repository;

import com.example.greenhouseapplication.backend.model.User;

import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

@Repository
public interface UserRepository extends MongoRepository<User,ObjectId> {

    Optional<User> findByUsername(String username);
    void deleteByUsername(String username);
    boolean existsByUsername(String username);


}

// @Repository
// public interface UserRepository extends MongoRepository<User, String> {
//     Optional<User> findByUsername(String name);
// }
