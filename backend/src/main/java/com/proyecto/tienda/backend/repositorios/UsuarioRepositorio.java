package com.proyecto.tienda.backend.repositorios;



import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.models.UsuarioModelo;

@Repository
public interface UsuarioRepositorio extends MongoRepository<UsuarioModelo, String> {

    // El optinal es porque ese usuario puede o no puede estar en la base de datos

    Optional<UsuarioModelo> findByEmail(String email);

    boolean existsByEmail(String email);

    void deleteBy_id(String _id);

    UsuarioModelo findBy_id(String _id);

    Optional<UsuarioModelo> findByRecuperarContrasenia(String recuperarContrasenia);
    
    Optional<UsuarioModelo> findByExpiracionRecuperarContrasenia(String expiracionRecuperarContrasenia);

    // // Nuevo método para guardar el código de recuperación y la fecha actual
    // void guardarCodigoRecuperacion(String _id, String codigoRecuperacion,
    // LocalDateTime fechaRecuperacion);

    // // Nuevo método para verificar si el código de recuperación es válido
    // boolean esCodigoRecuperacionValido(String _id, LocalDateTime fechaActual);

}
