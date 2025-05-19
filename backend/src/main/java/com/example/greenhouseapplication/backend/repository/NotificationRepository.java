package com.example.greenhouseapplication.backend.repository;

import com.example.greenhouseapplication.backend.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    // you already get CRUD + pagination + sorting for free
}
