package com.example.greenhouseapplication.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.greenhouseapplication.backend.model.Greenhouse;
import com.example.greenhouseapplication.backend.service.GreenhouseService;
import java.util.List;

@RestController
@RequestMapping("/api/greenhouses")
public class GreenhouseController {
    private final GreenhouseService service;
    public GreenhouseController(GreenhouseService service) {
        this.service = service;
    }

    @GetMapping
    public List<Greenhouse> getAll() {
        return service.getAllGreenhouses();
    }

       @PostMapping
    public ResponseEntity<Greenhouse> createGreenhouse(@RequestBody Greenhouse greenhouse) {
        return service.createGreenhouse(greenhouse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGreenhouse(@PathVariable String id) {
        service.deleteGreenhouse(id);
        return ResponseEntity.noContent().build();
    }
}