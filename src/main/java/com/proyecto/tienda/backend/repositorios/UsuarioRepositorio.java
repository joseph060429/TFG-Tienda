package com.proyecto.tienda.backend.repositorios;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.models.UsuarioModelo;

@Repository
public interface UsuarioRepositorio extends MongoRepository<UsuarioModelo, String> {

    // EL OPTIONAL SE UTILIZA PARA REPRESENTAR UN VALOR QUE PUEDE ESTAR PRESENTE O
    // NO.

    // CONSULTA PARA BUSCAR UN USUARIO POR SU CORREO ELECTRONICO
    Optional<UsuarioModelo> findByEmail(String email);

    // CONSULTA PARA COMPROBAR SI EXISTE UN USUARIO CON ESE EMAIL
    boolean existsByEmail(String email);

    // COSULTA PARA BORRAR UN USUARIO POR ID
    void deleteBy_id(String _id);

    // CONSULTA PARA BUSCAR UN USUARIO POR SU ID
    // UsuarioModelo findBy_id(String _id);

    // CONSULTA PARA BUSCAR UN USUARIO POR EL CAMPO QUE TENGO EN MI BASE DE DATOS
    // QUE SE LLAMA RECUPERAR CONTRASEÑA Y ES EL CODIGO DE RECUPERACION
    Optional<UsuarioModelo> findByRecuperarContrasenia(String recuperarContrasenia);

    // CONSULTA PARA BUSCAR UN USUARIO POR EL CAMPO QUE TENGO EN MI BASE DE DATOS
    // QUE SE LLAMA EXPIRACION RECUPERAR CONTRASEÑA
    Optional<UsuarioModelo> findByExpiracionRecuperarContrasenia(String expiracionRecuperarContrasenia);

    @Query(value = "{}", fields = "{ 'password' : 0 }")
    List<UsuarioModelo> findAllWithoutPassword();

}
