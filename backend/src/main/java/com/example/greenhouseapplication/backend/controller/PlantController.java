package com.example.greenhouseapplication.backend.controller;

import com.example.greenhouseapplication.backend.model.Plant;
import com.example.greenhouseapplication.backend.service.PlantService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/plants")
public class PlantController {

    private final PlantService plantService;

    public PlantController(PlantService plantService) {
        this.plantService = plantService;
    }

    /**
     * GET /api/plants
     * Returns every plant in the system (if you ever need it).
     */
    @GetMapping
    public List<Plant> getAllPlants() {
        return plantService.getAllPlants();
    }

    /**
     * GET /api/plants/greenhouse/{ghId}
     * Returns all plants belonging to a single greenhouse.
     */
    @GetMapping("/greenhouse/{ghId}")
    public List<Plant> getPlantsByGreenhouse(@PathVariable String ghId) {
        return plantService.getPlantsByGreenhouse(ghId);
    }
}
