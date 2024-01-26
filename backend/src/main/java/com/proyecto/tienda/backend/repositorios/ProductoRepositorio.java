package com.proyecto.tienda.backend.repositorios;

import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.models.Producto;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;


@Repository
public interface ProductoRepositorio extends MongoRepository <Producto, String> {

    boolean existsByIdentificador(String identificador);



    
}
