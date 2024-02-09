package com.proyecto.tienda.backend.service.ProductoServicio.AuthProductoServicio;

import com.proyecto.tienda.backend.DTO.DTOProducto.ActualizarProductoDTO;
import com.proyecto.tienda.backend.DTO.DTOProducto.CrearProductoDTO;
import com.proyecto.tienda.backend.models.Producto;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
// import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

// @Service
public interface AuthProductoServicio {

        // ResponseEntity<?> crearProducto(CrearProductoDTO crearProductoDTO); // HECHO

        ResponseEntity<?> crearProducto(CrearProductoDTO crearProductoDTO, MultipartFile file);


        String eliminarProducto(String _id);

        ResponseEntity<?> actualizarProducto(String _id, ActualizarProductoDTO actualizarProductoDTO);

        ResponseEntity<?> listarUnProducto(String _id);

        List<Map<String, Object>> buscarProductosAdmin(String descripcion, String categoria, String nombre,
                        String marca,
                        // BigDecimal precio,
                        int page, int size);

        List<Map<String, Object>> buscarProductosPorEspecificacionAdmin(String especificacion, int page, int size);

        List<Map<String, Object>> buscarProductosPorRangoDePrecio(double precioMin, double precioMax,
                        int page, int size);

        Page<Producto> listarProductosAdmin(int page, int size);

        // // Para subir imagen
        // String subirImagen(MultipartFile file) throws Exception;

        String subirImagen(MultipartFile file, CrearProductoDTO crearProductoDTO) throws Exception;

        // Probando Subir Imagen a la base de datos

        // ResponseEntity<?> crearProductoConImagen(CrearProductoDTO crearProductoDTO,
        // MultipartFile imagen);

}
