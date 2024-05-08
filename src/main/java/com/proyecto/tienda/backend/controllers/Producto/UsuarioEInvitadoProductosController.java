package com.proyecto.tienda.backend.controllers.Producto;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.proyecto.tienda.backend.service.ProductoServicio.UsuarioEInvitadoProductoServicio;

@RestController
public class UsuarioEInvitadoProductosController {

    @Autowired
    private UsuarioEInvitadoProductoServicio usuarioEInvitadoProductoServicio;

    // CONTROLADOR PARA LISTAR TODOS LOS PRODUCTOS Y QUE ME TRAIGA SOLO LOS CAMPOS
    // QUE QUIERO QUE MUESTRE AL USUARIO
    @GetMapping("/listarProductos")
    public ResponseEntity<List<Map<String, Object>>> listarProductos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {

        try {
            List<Map<String, Object>> productos = usuarioEInvitadoProductoServicio.listarProductos(page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            // jajajaja
            return ResponseEntity.status(500).body(null);
        }
    }

    // CONTROLADOR PARA BUSCAR UN PRODUCTO POR ID
    @GetMapping("/listarUnProducto")
    public ResponseEntity<?> listarUnProducto(@RequestParam("_id") String id) {

        try {
            ResponseEntity<?> response = usuarioEInvitadoProductoServicio.listarUnProducto(id);
            return ResponseEntity.ok(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error interno del servidor");
        }
    }

    // CONTROLADOR DE BUSQUEDA POR CAMPOS IMPORTANTES EN EL PRECIO TENGO QUE PONERLE
    // EL CAMPO EXACTO PARA QUE LO HAGA
    @GetMapping("/buscarPorCamposImportantes")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorCamposImportantes(
            @RequestParam(required = false) String descripcion,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) String marca,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        try {
            List<Map<String, Object>> productos = usuarioEInvitadoProductoServicio.buscarProductosPorCampos(descripcion,
                    categoria, nombre,
                    marca, page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // CONTROLADOR DE BUSQUEDA POR CUALQUIER ESPECIFICACION QUE PONGA EL USUARIO Y
    // LE MUESTRO LO QUE YO QUIERO QUE VEA
    @GetMapping("/buscarPorEspecificacion")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorEspecificacion(
            @RequestParam(required = false) String especificacion,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        try {
            List<Map<String, Object>> productos = usuarioEInvitadoProductoServicio.buscarProductosPorEspecificacion(
                    especificacion,
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

    // CONTROLADOR PARA BUSCAR POR RANGO DE PRECIO QUE PONGA EL USUARIO Y LE MUESTRO
    // LO QUE YO QUIERO QUE VEA
    @GetMapping("/buscarPorRangoDePrecio")
    public ResponseEntity<List<Map<String, Object>>> buscarProductosPorRangoDePrecio(
            @RequestParam(required = false) Optional<Double> precioMin,
            @RequestParam(required = false) Optional<Double> precioMax,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1000") int size) {
        try {
            // Obtener el valor o usar 0 si está ausente
            double min = precioMin.orElse(0.0);
            double max = precioMax.orElse(Double.MAX_VALUE);

            List<Map<String, Object>> productos = usuarioEInvitadoProductoServicio.buscarProductosPorRangoDePrecio(min,
                    max,
                    page, size);
            return ResponseEntity.ok(productos);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

}
