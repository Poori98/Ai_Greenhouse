package com.example.greenhouseapplication.backend.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document (collection = "Users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    
    @Field("user") 
    private String username;

    @Id
    private String id;
    
    
    private String email;
    private String password;
    private String role;

    public String getUsername() {
        return username;
    }

    

}
