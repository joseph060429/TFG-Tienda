package com.proyecto.tienda.backend.service.ProductoServicio;

import java.util.List;
import java.util.Map;

public interface ProductoServicio {

    List<Map<String, Object>> listarProductos(int page, int size);

    List<Map<String, Object>> buscarProductosPorCampos(String descripcion, String categoria, String nombre, String marca,
            int page, int size);

    List<Map<String, Object>> buscarProductosPorEspecificacion(String especificacion, int page, int size);

    List<Map<String, Object>> buscarProductosPorRangoDePrecio(double precioMin, double precioMax, int page, int size);
}
