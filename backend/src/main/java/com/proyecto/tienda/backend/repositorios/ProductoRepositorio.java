package com.proyecto.tienda.backend.repositorios;

import org.springframework.stereotype.Repository;
import com.proyecto.tienda.backend.models.ProductoModelo;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.domain.Pageable;
import java.util.Optional;

// ESTO SON LAS CONSULTAS CLAVES DE SPRING DATA, AL IGUAL QUE TODOS LOS REPOSITORIOS
@Repository
public interface ProductoRepositorio extends MongoRepository<ProductoModelo, String> {

  // CONSULTA PARA VER SI EXISTE UN IDENTIFICADOR DEL PRODUCTO IGNORANDO
  // MAYUSCULAS Y MINUSCULAS
  boolean existsByIdentificador(String identificador);

  // CONSULTA DE BUSQUEDA POR DESCRIPCION DEL PRODUCTO IGNORANDO MAYUSCULAS Y
  // MINUSCULAS
  Page<ProductoModelo> findByDescripcionProductoContainingIgnoreCase(String descripcion, Pageable pageable);

  // CONSULTA DE BUSQUEDA POR CATEGORIA DEL PRODUCTO IGNORANDO MAYUSCULAS Y
  // MINUSCULAS
  Page<ProductoModelo> findByCategoriaProductoIgnoreCase(String categoria, Pageable pageable);

  // CONSULTA DE BUSQUEDA POR NOMBRE DEL PRODUCTO IGNORANDO MAYUSCULAS Y
  // MINUSCULAS
  Page<ProductoModelo> findByNombreProductoContainingIgnoreCase(String nombre, Pageable pageable);

  // CONSULTA DE BUSQUEDA POR PRECIO DEL PRODUCTO
  Page<ProductoModelo> findByPrecioProducto(Double precio, Pageable pageable);

  // CONSULTA PERSONALIZADA EN MONGODB QUE BUSCA DOCUMENTOS DONDE EL CAMPO
  // PRECIOPRODUCTO ESTÉ ENTRE LOS VALORES PROPORCIONADOS
  // ($GTE SIGNIFICA "MAYOR O IGUAL QUE" Y $LTE SIGNIFICA "MENOR O IGUAL QUE").
  @Query("{ 'precioProducto' : { $gte: ?0, $lte: ?1 } }")
  Page<ProductoModelo> findByPrecioProductoBetween(double precioMin, double precioMax, Pageable pageable);

  // CONSULTA DE BUSQUEDA POR MARCA DEL PRODUCTO IGNORANDO MAYUSCULAS Y MINUSCULAS
  Page<ProductoModelo> findByMarcaProductoContainingIgnoreCase(String marca, Pageable pageable);

  // CONSULTA PARA BUSCAR UN PRODUCTO POR ID
  ProductoModelo findBy_id(String _id);

  // CONSULTA PARA ELIMINAR UN PRODUCTO
  Optional<ProductoModelo> deleteBy_id(String _id);

  // CONSULTA PERSONALIZADA EN MONGODB QUE BUSCA PRODUCTOS DONDE ALGUNO DE LOS CAMPOS
  // COINCIDA CON LA EXPRESIÓN REGULAR PROPORCIONADA, IGNORANDO MAYÚSCULAS Y MINÚSCULAS.
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

  // CONSULTA DE BUSQUEDA POR CUALQUIER ESPECIFICACION
  Page<ProductoModelo> findByAllFieldsContainingIgnoreCase(String especificacion, Pageable pageable);

}
