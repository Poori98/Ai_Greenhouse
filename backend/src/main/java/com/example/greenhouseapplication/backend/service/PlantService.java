package com.example.greenhouseapplication.backend.service;

import com.example.greenhouseapplication.backend.model.Plant;
import com.example.greenhouseapplication.backend.repository.PlantRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PlantService {
    private final PlantRepository plantRepo;

    public PlantService(PlantRepository plantRepo) {
        this.plantRepo = plantRepo;
    }

    /** Get all plants in a given greenhouse */
    public List<Plant> getPlantsByGreenhouse(String greenhouseId) {
        return plantRepo.findByGreenhouseId(greenhouseId);
    }

    /** (Optional) if you ever need all plants */
    public List<Plant> getAllPlants() {
        return plantRepo.findAll();
    }
}
