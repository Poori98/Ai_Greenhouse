package com.example.greenhouseapplication.backend.repository;

import com.example.greenhouseapplication.backend.model.MaintenanceTask;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceTaskRepository extends MongoRepository<MaintenanceTask, String> {
    // MongoRepository provides basic CRUD methods by default
    // You can add custom query methods here if needed
}

