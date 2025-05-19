package com.example.greenhouseapplication.backend.repository;

import com.example.greenhouseapplication.backend.model.Plant;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlantRepository extends MongoRepository<Plant, String> {
    List<Plant> findByGreenhouseId(String greenhouseId);
}
