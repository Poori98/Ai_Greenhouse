package com.example.greenhouseapplication.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;

@Document(collection = "Plants")
@Data
public class Plant {
    @Id
    private String id;
    private String greenhouseId;
    private String species;
    private String plantedDate;
    // …any other plant‐specific fields…
}
