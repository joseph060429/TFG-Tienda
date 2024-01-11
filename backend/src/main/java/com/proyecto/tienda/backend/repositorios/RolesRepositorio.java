package com.proyecto.tienda.backend.repositorios;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.proyecto.tienda.backend.models.ERol;
import com.proyecto.tienda.backend.models.Roles;


@Repository
public interface RolesRepositorio extends MongoRepository <Roles, String>{

    Optional <Roles> findByName(ERol name);

    Optional<Roles> findByName(String name);


}
