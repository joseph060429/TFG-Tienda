package com.proyecto.tienda.backend.service.ProductoServicio;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface UsuarioEInvitadoProductoServicio {

    // METODO PARA BUSCAR TODOS LOS PRODUCTOS
    List<Map<String, Object>> listarProductos(int page, int size);

    // METODO PARA BUSCAR TODOS LOS PRODUCTOS POR CAMPOS IMPORTANTES
    List<Map<String, Object>> buscarProductosPorCampos(String descripcion, String categoria, String nombre,
            String marca,
            int page, int size);

    // METODO PARA BUSCAR PRODUCTO POR CUALQUIER ESPECIFICACION
    List<Map<String, Object>> buscarProductosPorEspecificacion(String especificacion, int page, int size);

    // METODO PARA BUSCAR PRODUCTOS POR EL RANGO DE PRECIO
    List<Map<String, Object>> buscarProductosPorRangoDePrecio(double precioMin, double precioMax, int page, int size);

    // METODO PARA LISTAR UN PRODUCTO POR ID
    ResponseEntity<?> listarUnProducto(String _id);
}
