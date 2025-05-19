package com.example.greenhouseapplication.backend.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.greenhouseapplication.backend.model.Greenhouse;
import com.example.greenhouseapplication.backend.repository.GreenhouseRepository;
import java.util.List;

@Service
public class GreenhouseService {
    private final GreenhouseRepository greenhouseRepository;

    public GreenhouseService(GreenhouseRepository greenhouseRepository) {
        this.greenhouseRepository = greenhouseRepository;
    }

    public List<Greenhouse> getAllGreenhouses() {
        return greenhouseRepository.findAll();
    }

    public ResponseEntity<Greenhouse> createGreenhouse(@RequestBody Greenhouse greenhouse) {
        Greenhouse saved = greenhouseRepository.save(greenhouse);
        return ResponseEntity.status(201).body(saved);
    }

    public void deleteGreenhouse(String id) {
        if (!greenhouseRepository.existsById(id)) {
            throw new RuntimeException("Greenhouse not found with ID: " + id);
        }
        greenhouseRepository.deleteById(id);
    }
}
