package com.proyecto.tienda.backend.service.ProductoServicio;


import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.DTO.DTOProducto.CrearProductoDTO;
import com.proyecto.tienda.backend.UtilEnum.EProducto;
import com.proyecto.tienda.backend.models.Producto;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;

@Service
public class ProductoServicio {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    public ResponseEntity<?> crearProducto(CrearProductoDTO crearProductoDTO) {

        // Validar la categoría del producto
        if (!esCategoriaValida(crearProductoDTO.getCategoriaProducto())) {
            System.out.println("La categoría del producto no es válida " + crearProductoDTO.getCategoriaProducto());
            return ResponseEntity.badRequest().body("La categoría del producto no es válida");
        }

        //Creo el nuevo producto
        Producto nuevoProducto = construirProducto(crearProductoDTO);

        // Valido si el identificador ya existe en la base de datos
        String identificador = nuevoProducto.getIdentificador();
        System.out.println("Identificador a comprobar: " + identificador);

        if (productoRepositorio.existsByIdentificador(identificador)) {
            System.out.println("Identificador existe: " + identificador);
            return ResponseEntity.badRequest().body("Ya existe un producto con el mismo identificador");
        } else {
            System.out.println("Identificador NO existe en la base de datos: " + identificador);

            // Guardar el producto en la base de datos
            productoRepositorio.save(nuevoProducto);

            return ResponseEntity.ok("Producto creado exitosamente");
        }
    }

    // Metodo para comprobar si la categoria que le estoy poniendo es valida
    private boolean esCategoriaValida(String categoria) {
        // Obtengo todas las categorías del enum
        EProducto[] categoriasEnum = EProducto.values();

        // Verifico si la categoría del DTO coincide con alguna categoría del enum
        for (EProducto categoriaEnum : categoriasEnum) {
            if (categoriaEnum.name().equalsIgnoreCase(categoria)) {
                return true; // Categoría es válida
            }
        }

        return false; // Categoría no válida
    }

    private Producto construirProducto(CrearProductoDTO crearProductoDTO) {
        Producto nuevoProducto = new Producto();

        // Convierto el EProduct a String
        EProducto categoriaProductoEnum = EProducto.valueOf(crearProductoDTO.getCategoriaProducto());
        nuevoProducto.setCategoriaProducto(categoriaProductoEnum);

        nuevoProducto.setNombreProducto(crearProductoDTO.getNombreProducto().trim());
        nuevoProducto.setDescripcionProducto(crearProductoDTO.getDescripcionProducto().trim());
        nuevoProducto.setPrecioProducto(crearProductoDTO.getPrecioProducto());
        nuevoProducto.setDisponibilidadProducto(crearProductoDTO.getCantidadProducto() > 0);
        nuevoProducto.setCantidadProducto(crearProductoDTO.getCantidadProducto());
        nuevoProducto.setMarcaProducto(crearProductoDTO.getMarcaProducto().trim());
        nuevoProducto.setEspecificacionesTecnicas(crearProductoDTO.getEspecificacionesTecnicas().trim());

        // Construir el identificador con la categoría, nombre y marca separados por
        // guiones
        String identificador = construirIdentificador(crearProductoDTO);

        nuevoProducto.setIdentificador(identificador);
        System.out.println("Identificador: " + identificador);
        nuevoProducto.setImagenProducto(crearProductoDTO.getImagenProducto());

        if (nuevoProducto.get_id() == null) {
            nuevoProducto.set_id(UUID.randomUUID().toString());
        }

        return nuevoProducto;
    }

    // Identificador para no insertar el mismo producto 2 veces
    private String construirIdentificador(CrearProductoDTO crearProductoDTO) {
        String categoria = crearProductoDTO.getCategoriaProducto().toLowerCase();
        String nombre = crearProductoDTO.getNombreProducto().toLowerCase().replaceAll("\\s+", "");
        String marca = crearProductoDTO.getMarcaProducto().toLowerCase().replaceAll("\\s+", "");

        return String.format("%s-%s-%s", categoria, nombre, marca);
    }

}
