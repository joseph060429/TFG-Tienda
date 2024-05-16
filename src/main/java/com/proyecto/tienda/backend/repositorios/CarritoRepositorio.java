package com.proyecto.tienda.backend.repositorios;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.tienda.backend.models.CarritoModelo;

@Repository
public interface CarritoRepositorio extends MongoRepository<CarritoModelo, String> {

    CarritoModelo findByUsuario_Id(String usuarioId);
    
}
