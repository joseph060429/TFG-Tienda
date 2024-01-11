package com.proyecto.tienda.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "Roles")
public class Roles {

    @Id
    private String id;

    
    // private ERol name;

    private String name;
    
    public void setName(ERol eRol){
        this.name = eRol.toString();
    }
    public ERol getName(){
        return ERol.valueOf(this.name);
    }

   
}
