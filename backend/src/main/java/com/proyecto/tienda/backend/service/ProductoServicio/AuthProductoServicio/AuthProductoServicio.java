package com.proyecto.tienda.backend.service.ProductoServicio.AuthProductoServicio;

import com.proyecto.tienda.backend.DTO.DTOProducto.ActualizarProductoDTO;
import com.proyecto.tienda.backend.DTO.DTOProducto.CrearProductoDTO;
import com.proyecto.tienda.backend.models.ProductoModelo;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface AuthProductoServicio {

        // METODO PARA CREAR UN PRODUCTO
        ResponseEntity<?> crearProducto(CrearProductoDTO crearProductoDTO, MultipartFile file);

        // METODO PARA ELIMINAR UN PRODUCTO
        String eliminarProducto(String _id);

        // METODO PARA ACTUALIZAR UN PRODUCTO
        ResponseEntity<?> actualizarProducto(String _id, ActualizarProductoDTO actualizarProductoDTO,
                        MultipartFile file);

        // METODO PARA LISTAR UN PRODUCTO POR ID
        ResponseEntity<?> listarUnProducto(String _id);

        // METODO PARA BUSCAR TODOS LOS PRODUCTOS POR CAMPOS IMPORTANTES
        List<Map<String, Object>> buscarProductosAdmin(String descripcion, String categoria, String nombre,
                        String marca,
                        int page, int size);

        // METODO PARA BUSCAR TODOS LOS PRODUCTOS POR ESPECIFICACIONES
        List<Map<String, Object>> buscarProductosPorEspecificacionAdmin(String especificacion, int page, int size);

        // METODO PARA BUSCAR POR RANGO DE PRECIO
        List<Map<String, Object>> buscarProductosPorRangoDePrecio(double precioMin, double precioMax,
                        int page, int size);

        // METODO PARA LISTAR TODOS LOS PRODUCTOS
        Page<ProductoModelo> listarProductosAdmin(int page, int size);

}
