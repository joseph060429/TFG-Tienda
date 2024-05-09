package com.proyecto.tienda.backend.service.ProductoServicio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.text.Normalizer;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;

@Service
public class UsuarioEInvitadoProductoServicioImpl implements UsuarioEInvitadoProductoServicio {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    // IMPLEMENTACION DEL LISTAR TODOS LOS PRODUCTOS SOLO ME MUESTRA LO QUE QUIERO
    // QUE VEA EL USUARIO
    @Override
    public List<Map<String, Object>> listarProductos(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<ProductoModelo> productosPage;

        productosPage = productoRepositorio.findAll(pageable);

        List<Map<String, Object>> productosResponse = new ArrayList<>();

        for (ProductoModelo producto : productosPage.getContent()) {
            Map<String, Object> mapaProductos = new HashMap<>();
            mapaProductos.put("_id", producto.get_id());
            mapaProductos.put("descripcionProducto", producto.getDescripcionProducto());
            mapaProductos.put("categoriaProducto", producto.getCategoriaProducto());
            mapaProductos.put("nombreProducto", producto.getNombreProducto());
            mapaProductos.put("precioProducto", producto.getPrecioProducto());
            mapaProductos.put("marcaProducto", producto.getMarcaProducto());
            mapaProductos.put("especificacionesTecnicas", producto.getEspecificacionesTecnicas());
            mapaProductos.put("imagenProducto", producto.getImagenProducto());
            mapaProductos.put("disponibilidadProducto", producto.isDisponibilidadProducto());

            // CAMBIO
            productosResponse.add(mapaProductos);
        }
        return productosResponse;
    }

    @Override
    public ResponseEntity<?> listarUnProducto(String _id) {
        Optional<ProductoModelo> productOptional = productoRepositorio.findById(_id);
        if (productOptional.isPresent()) {
            ProductoModelo producto = productOptional.get();

            // List<Map<String, Object>> productosResponse = new ArrayList<>();
            Map<String, Object> mapaProductos = new HashMap<>();
            mapaProductos.put("_id", producto.get_id());
            mapaProductos.put("descripcionProducto", producto.getDescripcionProducto());
            mapaProductos.put("categoriaProducto", producto.getCategoriaProducto());
            mapaProductos.put("nombreProducto", producto.getNombreProducto());
            mapaProductos.put("precioProducto", producto.getPrecioProducto());
            mapaProductos.put("marcaProducto", producto.getMarcaProducto());
            mapaProductos.put("especificacionesTecnicas", producto.getEspecificacionesTecnicas());
            mapaProductos.put("imagenProducto", producto.getImagenProducto());
            mapaProductos.put("disponibilidadProducto", producto.isDisponibilidadProducto());

            return ResponseEntity.ok(mapaProductos);
        } else {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }
    }

    // IMPLEMENTACION DEL METODO PARA BUSCAR POR CAMPOS IMPORTANTES Y SOLO ME
    // MUESTRE LO QUE QUIERO QUE VEA EL
    // USUARIO
    @Override
    public List<Map<String, Object>> buscarProductosPorCampos(String descripcion, String categoria, String nombre,
            String marca,
            int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ProductoModelo> productosPage;

        if (categoria != null && !categoria.isEmpty()) {
            String normalizedCategoria = normalizeText(categoria);
            productosPage = productoRepositorio.findByCategoriaProductoIgnoreCase(normalizedCategoria, pageable);

        } else if (nombre != null && !nombre.isEmpty()) {
            String normalizedNombre = normalizeText(nombre);
            productosPage = productoRepositorio.findByNombreProductoContainingIgnoreCase(normalizedNombre, pageable);

        } else if (marca != null && !marca.isEmpty()) {
            String normalizedMarca = normalizeText(marca);
            productosPage = productoRepositorio.findByMarcaProductoContainingIgnoreCase(normalizedMarca, pageable);

        } else if (descripcion != null && !descripcion.isEmpty()) {
            String normalizedDescripcion = normalizeText(descripcion);
            productosPage = productoRepositorio.findByDescripcionProductoContainingIgnoreCase(normalizedDescripcion,
                    pageable);

        } else {
            productosPage = productoRepositorio.findAll(pageable);
        }

        List<Map<String, Object>> productosResponse = new ArrayList<>();

        for (ProductoModelo producto : productosPage.getContent()) {
            Map<String, Object> mapaProductos = new HashMap<>();
            mapaProductos.put("_id", producto.get_id());
            mapaProductos.put("descripcion", producto.getDescripcionProducto());
            mapaProductos.put("categoria", producto.getCategoriaProducto());
            mapaProductos.put("nombre", producto.getNombreProducto());
            mapaProductos.put("precio", producto.getPrecioProducto());
            mapaProductos.put("marca", producto.getMarcaProducto());
            mapaProductos.put("especificacionesTecnicas", producto.getEspecificacionesTecnicas());
            mapaProductos.put("imagenProducto", producto.getImagenProducto());
            mapaProductos.put("disponibilidad", producto.isDisponibilidadProducto());

            productosResponse.add(mapaProductos);
        }
        return productosResponse;
    }

    // IMPLEMENTACION DEL METODO PARA BUSCAR PRODUCTO POR CUALQUIER ESPECIFICACION
    @Override
    public List<Map<String, Object>> buscarProductosPorEspecificacion(String especificacion, int page, int size) {
        System.out.println("Especificación: " + especificacion);

        // ConfigurO la paginación
        Pageable pageable = PageRequest.of(page, size);

        // NormalizO la especificación
        String normalizedEspecificacion = normalizeText(especificacion);

        // Utilizo el método del repositorio con paginación
        Page<ProductoModelo> productosPage = productoRepositorio.findByAllFieldsContainingIgnoreCase(
                normalizedEspecificacion,
                pageable);

        // Transformo los productos con los campos que quiero en la respuesta
        List<Map<String, Object>> productosResponse = new ArrayList<>();

        for (ProductoModelo producto : productosPage.getContent()) {
            Map<String, Object> mapaProductos = new HashMap<>();
            mapaProductos.put("_id", producto.get_id());
            mapaProductos.put("descripcionProducto", producto.getDescripcionProducto());
            mapaProductos.put("categoriaProducto", producto.getCategoriaProducto());
            mapaProductos.put("nombreProducto", producto.getNombreProducto());
            mapaProductos.put("precioProducto", producto.getPrecioProducto());
            mapaProductos.put("marcaProducto", producto.getMarcaProducto());
            mapaProductos.put("especificacionesTecnicas", producto.getEspecificacionesTecnicas());
            mapaProductos.put("imagenProducto", producto.getImagenProducto());
            mapaProductos.put("disponibilidadProducto", producto.isDisponibilidadProducto());

            productosResponse.add(mapaProductos);
        }

        return productosResponse;
    }

    // METODO PARA NORMALIZAR LOS TEXTOS QUE PONGA EL USUARIO Y ME BUSQUE SIN TILDE
    // LOS CAMPOS
    private String normalizeText(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }

    // IMPLEMENTACION DEL METODO PARA HACER UNA BUSQUEDA DE PRECIO
    @Override
    public List<Map<String, Object>> buscarProductosPorRangoDePrecio(double precioMin, double precioMax, int page,
            int size) {
        Pageable pageable = PageRequest.of(page, size);

        try {
            Page<ProductoModelo> productosPage = productoRepositorio.findByPrecioProductoBetween(precioMin, precioMax,
                    pageable);

            List<Map<String, Object>> productosResponse = new ArrayList<>();

            for (ProductoModelo producto : productosPage.getContent()) {
                Map<String, Object> mapaProductos = new HashMap<>();
                mapaProductos.put("_id", producto.get_id());
                mapaProductos.put("descripcion", producto.getDescripcionProducto());
                mapaProductos.put("categoria", producto.getCategoriaProducto());
                mapaProductos.put("nombre", producto.getNombreProducto());
                mapaProductos.put("precio", producto.getPrecioProducto());
                mapaProductos.put("marca", producto.getMarcaProducto());
                mapaProductos.put("especificacionesTecnicas", producto.getEspecificacionesTecnicas());
                mapaProductos.put("imagenProducto", producto.getImagenProducto());
                mapaProductos.put("disponibilidad", producto.isDisponibilidadProducto());

                productosResponse.add(mapaProductos);
            }
            return productosResponse;
        } catch (Exception e) {
            e.printStackTrace();
            // Mensaje de error, DEVUELVO LA LISTA VACÍA
            System.out.println("Error al buscar productos por rango de precio: " + e.getMessage());
            return Collections.emptyList();
        }
    }

}
