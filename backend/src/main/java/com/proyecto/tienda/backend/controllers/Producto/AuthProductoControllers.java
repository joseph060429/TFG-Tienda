package com.proyecto.tienda.backend.controllers.Producto;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.tienda.backend.DTO.DTOProducto.ActualizarProductoDTO;
import com.proyecto.tienda.backend.DTO.DTOProducto.CrearProductoDTO;
import com.proyecto.tienda.backend.models.Producto;
import com.proyecto.tienda.backend.service.ProductoServicio.AuthProductoServicio;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/admin/productos")
@PreAuthorize("hasRole('ADMIN')")
public class AuthProductoControllers {

    @Autowired
    private AuthProductoServicio authproductoServicio;

    // Crear producto
    @PostMapping("/crearProducto")
    public ResponseEntity<?> crearProducto(@Valid @RequestBody CrearProductoDTO crearProductoDTO) {
        try {
            // Validar y crear el producto
            ResponseEntity<?> response = authproductoServicio.crearProducto(crearProductoDTO);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            // Manejar cualquier excepción general y devolver una respuesta de error
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // Eliminar producto
    @DeleteMapping("/eliminarProducto")
    public ResponseEntity<String> eliminarProducto(@RequestParam("id") String id) {
        try {
            String resultadoEliminacion = authproductoServicio.eliminarProducto(id);

            if ("Producto eliminado correctamente".equals(resultadoEliminacion)) {
                return ResponseEntity.ok(resultadoEliminacion);
            } else {
                return ResponseEntity.status(404).body("El producto no existe o no se pudo eliminar");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar el producto");
        }
    }

    // Actualizar producto
    @PatchMapping("/actualizarProducto")
    public ResponseEntity<?> actualizarProducto(@RequestParam("id") String id,
            @Valid @RequestBody ActualizarProductoDTO actualizarProductoDTO) {
        try {
            ResponseEntity<?> response = authproductoServicio.actualizarProducto(id, actualizarProductoDTO);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // Buscar un producto por id
    @GetMapping("/listarUnProducto")
    public ResponseEntity<?> listarUnProducto(@RequestParam("id") String id) {

        try {
            ResponseEntity<?> response = authproductoServicio.listarUnProducto(id);
            return ResponseEntity.ok(response.getBody());

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    
    // Listar todos los productos ADMIN
    @GetMapping("/listarProductos")
    public ResponseEntity<List<Producto>> listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<Producto> productosPage = authproductoServicio.listarProductosAdmin(page, size);
        List<Producto> productos = productosPage.getContent();
        return ResponseEntity.ok(productos);
    }

    // Busqueda por campos importantes EN EL PRECIO TENGO QUE PONERLE EL CAMPO, me
    // muestra todos los campos porque es
    // EXACTO PARA QUE LO HAGA
    @GetMapping("/buscarPorCamposImportantes")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorCamposImportantes(
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) BigDecimal precio,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Boolean disponibilidad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> productos = authproductoServicio.buscarProductosAdmin(descripcion, categoria,
                    nombre,
                    precio, marca, page, size);
            // Se puede quitar el for pero lo dejo para imprimir en pantalla lo que me sale
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Busqueda por cualquier especificacion que ponga el ADMIN y le muestro TODOS
    // LOS CAMPOS
    @GetMapping("/buscarProductosPorEspecificacion")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorEspecificacionAdmin(
            @RequestParam(required = false) String especificacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> productos = authproductoServicio.buscarProductosPorEspecificacionAdmin(
                    especificacion,
                    page, size);
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

            List<Map<String, Object>> productos = authproductoServicio.buscarProductosPorRangoDePrecio(min, max,
                    page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
