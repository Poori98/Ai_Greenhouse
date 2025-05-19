package com.example.greenhouseapplication.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "Greenhouses")
@Data
public class Greenhouse {
    @Id
    private String id;          // Unique identifier for the greenhouse
    private String name;        // Name of the greenhouse
    private String location;    // Physical location or address
    private String size;        // Size of greenhouse
    private int plantCount;     // Stores count of plants
    //private String description; // Brief description of the greenhouse
    // Additional fields can be added as needed (e.g., capacity, owner, etc.)
}