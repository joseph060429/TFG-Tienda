package com.proyecto.tienda.backend.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.proyecto.tienda.backend.UtilEnum.ERol;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(value = "Roles")
public class RolesModelo {

    @Id
    private String id;

    private String name;
    
    //METODO PARA AÑADIRLE EL NOBRE DEL ROL DEL EROL
    public void setName(ERol eRol){
         // Establezco el nombre (en forma de cadena) del enum ERol en mi atributo 'name'
        this.name = eRol.toString();
    }

    //METODO PARA OBTENER EL NOBRE DEL ROL
    public ERol getName(){
        // Obtengo el valor del enum ERol a partir de la cadena almacenada en mi atributo 'name'
        return ERol.valueOf(this.name);
    }

   
}
