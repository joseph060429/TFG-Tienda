package com.proyecto.tienda.backend.repositorios;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.models.CarritoModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;

@Repository
public interface CarritoRepositorio extends MongoRepository<CarritoModelo, String> {

   Optional<CarritoModelo> findByUsuario(UsuarioModelo usuario);
    
}
