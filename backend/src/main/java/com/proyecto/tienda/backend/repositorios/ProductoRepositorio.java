package com.proyecto.tienda.backend.repositorios;

import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.models.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

//Esto son busquedas claves de Spring Data, al igual que todos los repositorios
@Repository
public interface ProductoRepositorio extends MongoRepository<Producto, String> {

  boolean existsByIdentificador(String identificador);

  Page<Producto> findByDescripcionProductoContainingIgnoreCase(String descripcion, Pageable pageable);

  Page<Producto> findByCategoriaProductoIgnoreCase(String categoria, Pageable pageable);

  Page<Producto> findByNombreProductoContainingIgnoreCase(String nombre, Pageable pageable);

  Page<Producto> findByPrecioProducto(Double precio, Pageable pageable);

  // Consulta personalizadas en MongoDB que busca documentos donde el campo
  // precioProducto esté entre los valores proporcionados
  // ($gte significa "mayor o igual que" y $lte significa "menor o igual que").
  @Query("{ 'precioProducto' : { $gte: ?0, $lte: ?1 } }")
  Page<Producto> findByPrecioProductoBetween(double precioMin, double precioMax, Pageable pageable);

  @Query("{ 'precioProducto' : { $gte: ?0, $lte: ?1 } }")
  Page<Producto> findByPrecioProductoBetweenOrderByPrecioProductoDesc(double precioMax, double precioMin,
      Pageable pageable);

  Page<Producto> findByMarcaProductoContainingIgnoreCase(String marca, Pageable pageable);

  Producto findBy_id(String _id);

  Optional<Producto> deleteBy(String _id);

  @Query("{" +
      "'$or': [" +
      "{ 'categoriaProducto': { '$regex' : ?0, '$options': 'i' } }," +
      "{ 'nombreProducto': { '$regex' : ?0, '$options': 'i' } }," +
      "{ 'descripcionProducto': { '$regex' : ?0, '$options': 'i' } }," +
      "{ 'precioProducto': { '$regex' : ?0, '$options': 'i' } }," +
      "{ 'disponibilidadProducto': { '$regex' : ?0, '$options': 'i' } }," +
      "{ 'cantidadProducto': { '$regex' : ?0, '$options': 'i' } }," +
      "{ 'marcaProducto': { '$regex' : ?0, '$options': 'i' } }," +
      "{ 'especificacionesTecnicas': { '$regex' : ?0, '$options': 'i' } }," +
      "]" +
      "}")

  Page<Producto> findByAllFieldsContainingIgnoreCase(String especificacion, Pageable pageable);

}
