package com.proyecto.tienda.backend.repositorios;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.models.CarritoModelo;


@Repository
public interface CarritoRepositorio extends MongoRepository<CarritoModelo, String> {

   // CONSULTA PARA VERIFICAR SI UN PRODUCTO YA ESTA ALMACENADO EN EL CARRITO DE ESE USUARIO
   Optional<CarritoModelo> findByIdUsuarioAndIdProducto(String idUsuario, String idProducto);
    
}
