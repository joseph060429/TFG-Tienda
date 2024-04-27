package com.proyecto.tienda.backend.repositorios;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.UtilEnum.ERol;
import com.proyecto.tienda.backend.models.RolesModelo;

@Repository
public interface RolesRepositorio extends MongoRepository<RolesModelo, String> {

    // CONSULTA PARA OBTENER EL ROL POR SU NOMBRE
    Optional<RolesModelo> findByName(ERol name); // LA DIFERENCIA ENTRE AMBAS ES EL TIPO DE PARAMETRO QUE SE ACEPTA

    // CONSULTA PARA OBTENER EL ROL POR SU NOMBRE
    Optional<RolesModelo> findByName(String name);

}
