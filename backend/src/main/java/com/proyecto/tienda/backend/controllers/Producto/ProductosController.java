package com.proyecto.tienda.backend.controllers.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.service.ProductoServicio.ProductoServicio;

@RestController
public class ProductosController {

    @Autowired
    private ProductoServicio productoServicio;

    // Listar todos los productos y que me traiga solo los campos que quiero que
    // muestre
    @GetMapping("/listarProductos")
    public ResponseEntity<List<Map<String, Object>>> listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> productos = productoServicio.listarProductos(page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Busqueda por campos importantes EN EL PRECIO TENGO QUE PONERLE EL CAMPO
    // EXACTO PARA QUE LO HAGA
    @GetMapping("/buscarPorCamposImportantes")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorCamposImportantes(
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String marca,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> productos = productoServicio.buscarProductos(descripcion, categoria, nombre,
                    marca, page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Busqueda por cualquier especificacion que ponga el usuario y le muestro al
    // usuario solo lo que yo quiero que vea
    @GetMapping("/buscarPorEspecificacion")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorEspecificacion(
            @RequestParam(required = false) String especificacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> productos = productoServicio.buscarProductosPorEspecificacion(especificacion,
                    page, size);
            // Se puede quitar el for pero lo dejo para imprimir en pantalla lo que me sale
            // for (Map<String, Object> producto : productos) {
            // System.out.println("----------------------------------------");
            // System.out.println("Descripción " + producto.get("descripcion"));
            // System.out.println("Categoría " + producto.get("categoria"));
            // System.out.println("Nombre " + producto.get("nombre"));
            // System.out.println("Precio " + producto.get("precio"));
            // System.out.println("Marca " + producto.get("marca"));
            // System.out.println("Especificaciones Técnicas " +
            // producto.get("especificacionesTecnicas"));
            // System.out.println("Imagen " + producto.get("imagenProducto"));
            // }

            // System.out.println("Fin de la lista de productos");
            // System.out.println("Tamaño de la lista: " + productos.size());
            // System.out.println("----------------------------------------");
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Para buscar por rango de precio
    @GetMapping("/buscarPorRangoDePrecio")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorRangoDePrecio(
            @RequestParam(required = false) Optional<Double> precioMin,
            @RequestParam(required = false) Optional<Double> precioMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            // Obtener el valor o usar 0 si está ausente
            double min = precioMin.orElse(0.0);
            double max = precioMax.orElse(Double.MAX_VALUE);

            List<Map<String, Object>> productos = productoServicio.buscarProductosPorRangoDePrecio(min, max,
                    page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
