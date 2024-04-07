package com.proyecto.tienda.backend.controllers.Producto;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.proyecto.tienda.backend.DTO.DTOProducto.ActualizarProductoDTO;
import com.proyecto.tienda.backend.DTO.DTOProducto.CrearProductoDTO;
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.service.ProductoServicio.AdminProductoServicio.AdminProductoServicio;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin/productos")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProductoControllers {

    @Autowired
    private AdminProductoServicio adminProductoServicio;

    // CONTROLADOR PARA CREAR UN PRODUCTO
    @PostMapping("/crearProducto")
    public ResponseEntity<?> crearProducto(@Valid @ModelAttribute CrearProductoDTO crearProductoDTO,
            @RequestParam("img") MultipartFile file) {
        try {

            // Validar y crear el producto
            ResponseEntity<?> response = adminProductoServicio.crearProducto(crearProductoDTO, file);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // CONTROLADOR PARA ACTUALIZAR UN PRODUCTO
    @PatchMapping("/actualizarProducto")
    public ResponseEntity<?> actualizarProducto(@RequestParam("id") String id,
            @RequestParam(required = false, value = "img") MultipartFile file,
            @Valid @ModelAttribute ActualizarProductoDTO actualizarProductoDTO) {
        try {
            ResponseEntity<?> response = adminProductoServicio.actualizarProducto(id, actualizarProductoDTO, file);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // CONTROLADOR PARA ELIMINAR UN PRODUCTO
    @DeleteMapping("/eliminarProducto")
    public ResponseEntity<String> eliminarProducto(@RequestParam("id") String id) {
        try {
            String resultadoEliminacion = adminProductoServicio.eliminarProducto(id);

            if ("Producto eliminado correctamente".equals(resultadoEliminacion)) {
                return ResponseEntity.ok(resultadoEliminacion);
            } else {
                return ResponseEntity.status(404).body("El producto no existe o no se pudo eliminar");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al eliminar el producto");
        }
    }

    // CONTROLADOR PARA BUSCAR UN PRODUCTO POR ID
    @GetMapping("/listarUnProducto")
    public ResponseEntity<?> listarUnProducto(@RequestParam("id") String id) {

        try {
            ResponseEntity<?> response = adminProductoServicio.listarUnProducto(id);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // CONTROLADOR PARA LISTAR TODOS LOS PRODUCTOS SIENDO ADMIN
    @GetMapping("/listarProductos")
    public ResponseEntity<List<ProductoModelo>> listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<ProductoModelo> productosPage = adminProductoServicio.listarProductosAdmin(page, size);
        List<ProductoModelo> productos = productosPage.getContent();
        return ResponseEntity.ok(productos);
    }

    // CONTROLADOR PARA BUSCAR UN PRODUCTO POR CAMPOS IMPORTANTES
    @GetMapping("/buscarPorCamposImportantes")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorCamposImportantes(
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String nombre,
            // @RequestParam(required = false) BigDecimal precio,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) Boolean disponibilidad,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> productos = adminProductoServicio.buscarProductosAdmin(descripcion, categoria,
                    nombre, marca, page, size);
            // precio,);
            // Se puede quitar el for pero lo dejo para imprimir en pantalla lo que me sale
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // CONTROLADOR PARA BUSCAR PRODUCTOS POR CUALQUIER ESPECIFICACION DEL ADMIN Y
    // MOSTRAR TODOS LOS CAMPOS
    @GetMapping("/buscarProductosPorEspecificacion")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorEspecificacionAdmin(
            @RequestParam(required = false) String especificacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            List<Map<String, Object>> productos = adminProductoServicio.buscarProductosPorEspecificacionAdmin(
                    especificacion,
                    page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // CONTROLADOR PARA BUSCAR PRODUCTOS POR RANGO DE PRECIO
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

            List<Map<String, Object>> productos = adminProductoServicio.buscarProductosPorRangoDePrecio(min, max,
                    page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
