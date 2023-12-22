package com.proyecto.tienda.backend.repositorios;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.tienda.backend.models.Usuarios;
// import java.util.List;




@Repository
public interface UsuarioRepositorio extends MongoRepository <Usuarios, String>{

    //El optinal es porque ese usuario puede o no puede estar en la base de datos

    Optional<Usuarios> findByEmail(String email);  

    boolean existsByEmail(String email);

    void deleteBy_id(String _id);

    Usuarios findBy_id(String _id);


    
    





    

} 
