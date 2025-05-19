package com.example.greenhouseapplication.backend.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.greenhouseapplication.backend.model.Greenhouse;

public interface GreenhouseRepository extends MongoRepository<Greenhouse, String> {
}
