package com.proyecto.tienda.backend.service.ProductoServicio.AuthProductoServicio;

import java.io.File;
import java.nio.file.*;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.proyecto.tienda.backend.DTO.DTOProducto.ActualizarProductoDTO;
import com.proyecto.tienda.backend.DTO.DTOProducto.CrearProductoDTO;
import com.proyecto.tienda.backend.UtilEnum.EProducto;
import com.proyecto.tienda.backend.models.Producto;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;

@Service
public class AuthProductoServicioImpl implements AuthProductoServicio {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    // Construccion del producto
    private Producto construirProducto(CrearProductoDTO crearProductoDTO, MultipartFile file) {

        Producto nuevoProducto = new Producto();

        try {

            EProducto categoriaProductoEnum = EProducto.valueOf(crearProductoDTO.getCategoriaProducto());
            nuevoProducto.setCategoriaProducto(categoriaProductoEnum);

            nuevoProducto.setNombreProducto(normalizeText(crearProductoDTO.getNombreProducto().trim()));
            nuevoProducto.setDescripcionProducto(normalizeText(crearProductoDTO.getDescripcionProducto().trim()));
            nuevoProducto.setPrecioProducto(crearProductoDTO.getPrecioProducto());
            nuevoProducto.setDisponibilidadProducto(crearProductoDTO.getCantidadProducto() > 0);
            nuevoProducto.setCantidadProducto(crearProductoDTO.getCantidadProducto());
            nuevoProducto.setMarcaProducto(normalizeText(crearProductoDTO.getMarcaProducto().trim()));
            nuevoProducto
                    .setEspecificacionesTecnicas(normalizeText(crearProductoDTO.getEspecificacionesTecnicas().trim()));

            // Construir el identificador
            String identificador = construirIdentificador(crearProductoDTO);
            nuevoProducto.setIdentificador(normalizeText(identificador));

            // Construir la imagen
            String nombreImagen = subirImagen(file, crearProductoDTO);
            nuevoProducto.setImagenProducto(nombreImagen);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al subir el archivo: " + e.getMessage());
        }

        if (nuevoProducto.get_id() == null) {
            nuevoProducto.set_id(UUID.randomUUID().toString());
        }

        return nuevoProducto;
    }

    // Crear Producto
    public ResponseEntity<?> crearProducto(CrearProductoDTO crearProductoDTO, MultipartFile file) {
        try {
            // Validar la categoría del producto
            if (!esCategoriaValida(crearProductoDTO.getCategoriaProducto())) {
                System.out.println("La categoría del producto no es válida " + crearProductoDTO.getCategoriaProducto());
                return ResponseEntity.badRequest().body("La categoría del producto no es válida");
            }

            // Construir el nuevo producto con la subida de la imagen
            Producto nuevoProducto = construirProducto(crearProductoDTO, file);

            // Valido si el identificador ya existe en la base de datos
            String identificador = nuevoProducto.getIdentificador();

            if (productoRepositorio.existsByIdentificador(identificador)) {
                return ResponseEntity.badRequest().body("Ya existe un producto con el mismo identificador");
            } else {
                // Guardar el producto en la base de datos
                productoRepositorio.save(nuevoProducto);

                return ResponseEntity.ok("Producto creado exitosamente");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al crear el producto: " + e.getMessage());
        }
    }

    @Override
    public String eliminarProducto(String _id) {

        Optional<Producto> productOptional = productoRepositorio.findById(_id);

        if (productOptional.isPresent()) {
            Producto producto = productOptional.get();
            productoRepositorio.deleteBy(producto.get_id());
            return "Producto eliminado correctamente";
        } else {
            return "Producto no encontrado";
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

    // Identificador para no insertar el mismo producto 2 veces en la creacion
    private String construirIdentificador(CrearProductoDTO crearProductoDTO) {
        String categoria = crearProductoDTO.getCategoriaProducto().toLowerCase();
        String nombre = crearProductoDTO.getNombreProducto().toLowerCase().replaceAll("\\s+", "");
        String marca = crearProductoDTO.getMarcaProducto().toLowerCase().replaceAll("\\s+", "");

        return String.format("%s-%s-%s", categoria, nombre, marca);
    }

    @Override
    public ResponseEntity<?> actualizarProducto(String _id, ActualizarProductoDTO actualizarProductoDTO) {
        Optional<Producto> productOptional = productoRepositorio.findById(_id);

        if (productOptional.isPresent()) {
            // Obtengo el objeto de la base de datos
            Producto producto = productOptional.get();

            // Actualizo la categoria
            if (actualizarProductoDTO.getCategoriaProducto() != null) {
                EProducto categoriaProductoEnum = EProducto.valueOf(actualizarProductoDTO.getCategoriaProducto());
                producto.setCategoriaProducto(categoriaProductoEnum);
            }

            // nuevoProducto.setNombreProducto(normalizeText(crearProductoDTO.getNombreProducto().trim()));

            // Actualizo el nombre del producto
            String nombreProducto = actualizarProductoDTO.getNombreProducto();
            if (nombreProducto != null && !nombreProducto.isEmpty()) {
                // producto.setNombreProducto(nombreProducto.trim());
                producto.setNombreProducto(normalizeText(actualizarProductoDTO.getNombreProducto().trim()));
            }
            // Actualizo la descripcion del producto
            String descripcionProducto = actualizarProductoDTO.getDescripcionProducto();
            if (descripcionProducto != null && !descripcionProducto.isEmpty()) {
                producto.setDescripcionProducto(normalizeText(actualizarProductoDTO.getDescripcionProducto().trim()));
            }
            // Actualizo la marca del producto
            String marcaProducto = actualizarProductoDTO.getMarcaProducto();
            if (marcaProducto != null && !marcaProducto.isEmpty()) {
                producto.setMarcaProducto(normalizeText(actualizarProductoDTO.getMarcaProducto().trim()));
            }
            // Actualizo el precio del producto
            Double precioProducto = actualizarProductoDTO.getPrecioProducto();
            if (precioProducto != null) {
                producto.setPrecioProducto(precioProducto);
            }
            // Actualizo la cantidad del producto
            Integer cantidadProducto = actualizarProductoDTO.getCantidadProducto();
            if (cantidadProducto != null) {
                if (cantidadProducto > 0) {
                    producto.setDisponibilidadProducto(true);
                } else if (cantidadProducto == 0) {
                    producto.setDisponibilidadProducto(false);
                }
                producto.setCantidadProducto(cantidadProducto);
            }
            // Actualizo las especificaciones tecnicas
            String especificacionesTecnicas = actualizarProductoDTO.getEspecificacionesTecnicas();
            if (especificacionesTecnicas != null) {
                producto.setEspecificacionesTecnicas(
                        normalizeText(actualizarProductoDTO.getEspecificacionesTecnicas().trim()));
            }
            // Actualizo la imagen del producto
            String imagenProducto = actualizarProductoDTO.getImagenProducto();
            if (imagenProducto != null) {
                producto.setImagenProducto(imagenProducto.trim());
            }

            // Construir el identificador con la categoría, nombre y marca separados por
            // guiones
            String identificadorActualizado = construirIdentificadorActualizado(actualizarProductoDTO, producto);
            producto.setIdentificador(normalizeText(identificadorActualizado));

            productoRepositorio.save(producto);

            return ResponseEntity.ok("Producto actualizado correctamente");
        } else {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }
    }

    // Identificador para no insertar el mismo producto 2 veces en la actualizacion
    private String construirIdentificadorActualizado(ActualizarProductoDTO actualizarProductoDTO,
            Producto productoExistente) {

        String categoria = "";
        if (actualizarProductoDTO.getCategoriaProducto() != null) {
            categoria = actualizarProductoDTO.getCategoriaProducto().toLowerCase().replaceAll("\\s+", "");
        } else {
            categoria = productoExistente.getCategoriaProducto().toString().toLowerCase().replaceAll("\\s+", "");
        }

        String nombre = "";
        if (actualizarProductoDTO.getNombreProducto() != null) {
            nombre = actualizarProductoDTO.getNombreProducto().toLowerCase().replaceAll("\\s+", "");

        } else {
            nombre = productoExistente.getNombreProducto().toLowerCase().replaceAll("\\s+", "");
        }

        String marca = "";
        if (actualizarProductoDTO.getMarcaProducto() != null) {
            marca = actualizarProductoDTO.getMarcaProducto().toLowerCase().replaceAll("\\s+", "");
        } else {
            marca = productoExistente.getMarcaProducto().toLowerCase().replaceAll("\\s+", "");
        }

        return String.format("%s-%s-%s", categoria, nombre, marca);
    }

    // // Metodo para listar un producto por id:
    @Override
    public ResponseEntity<?> listarUnProducto(String _id) {
        Optional<Producto> productOptional = productoRepositorio.findById(_id);
        if (productOptional.isPresent()) {
            System.out.println("Producto: " + productOptional.get());
            return ResponseEntity.ok(productOptional);
        } else {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }
    }

    // Listar todos los productos
    @Override
    public Page<Producto> listarProductosAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productoRepositorio.findAll(pageable);
    }

    // // // Busqueda por campos importantes y ME MUESTRA TODOS LOS CAMPOS PORQUE ES
    // PARA EL ADMIN
    @Override
    public List<Map<String, Object>> buscarProductosAdmin(
            // BigDecimal precio,
            String descripcion,
            String categoria, String nombre,
            String marca,
            int page, int size) {

        // System.out.println("Descripcion: " + descripcion);
        // System.out.println("Categoría: " + categoria);
        // System.out.println("Nombre: " + nombre);
        // System.out.println("Precio: " + precio);
        // System.out.println("Marca: " + marca);

        Pageable pageable = PageRequest.of(page, size);

        Page<Producto> productosPage;

        if (categoria != null && !categoria.isEmpty()) {
            String normalizedCategoria = normalizeText(categoria);
            productosPage = productoRepositorio.findByCategoriaProductoIgnoreCase(normalizedCategoria,
                    pageable);

        } else if (nombre != null && !nombre.isEmpty()) {
            String normalizedNombre = normalizeText(nombre);
            productosPage = productoRepositorio.findByNombreProductoContainingIgnoreCase(normalizedNombre,
                    pageable);

        } else if (marca != null && !marca.isEmpty()) {
            String normalizedMarca = normalizeText(marca);
            productosPage = productoRepositorio.findByMarcaProductoContainingIgnoreCase(normalizedMarca,
                    pageable);

        } else if (descripcion != null && !descripcion.isEmpty()) {
            String normalizedDescripcion = normalizeText(descripcion);
            productosPage = productoRepositorio.findByDescripcionProductoContainingIgnoreCase(normalizedDescripcion,
                    pageable);

        } else {
            productosPage = productoRepositorio.findAll(pageable);
        }

        List<Map<String, Object>> productosResponse = new ArrayList<>();

        for (Producto producto : productosPage.getContent()) {
            Map<String, Object> mapaProductos = new HashMap<>();
            mapaProductos.put("id", producto.get_id());
            mapaProductos.put("descripcion", producto.getDescripcionProducto());
            mapaProductos.put("categoria", producto.getCategoriaProducto());
            mapaProductos.put("nombre", producto.getNombreProducto());
            mapaProductos.put("precio", producto.getPrecioProducto());
            mapaProductos.put("marca", producto.getMarcaProducto());
            mapaProductos.put("especificacionesTecnicas",
                    producto.getEspecificacionesTecnicas());
            mapaProductos.put("imagenProducto", producto.getImagenProducto());
            mapaProductos.put("disponibilidad", producto.isDisponibilidadProducto());
            mapaProductos.put("cantidad", producto.getCantidadProducto());
            mapaProductos.put("identificador", producto.getIdentificador());

            productosResponse.add(mapaProductos);
        }
        return productosResponse;
    }

    // Metodo para buscar producto por cualquier especificacion y que le muestre
    // todos los campos al admin
    @Override
    public List<Map<String, Object>> buscarProductosPorEspecificacionAdmin(String especificacion, int page, int size) {
        System.out.println("Especificación: " + especificacion);

        // ConfigurO la paginación
        Pageable pageable = PageRequest.of(page, size);

        // NormalizO la especificación
        String normalizedEspecificacion = normalizeText(especificacion);

        // UtilizO el método del repositorio con paginación
        Page<Producto> productosPage = productoRepositorio.findByAllFieldsContainingIgnoreCase(normalizedEspecificacion,
                pageable);

        // Transformo los productos con los campos que quiero en la respuesta
        List<Map<String, Object>> productosResponse = new ArrayList<>();

        for (Producto producto : productosPage.getContent()) {
            Map<String, Object> mapaProductos = new HashMap<>();
            mapaProductos.put("id", producto.get_id());
            mapaProductos.put("descripcion", producto.getDescripcionProducto());
            mapaProductos.put("categoria", producto.getCategoriaProducto());
            mapaProductos.put("nombre", producto.getNombreProducto());
            mapaProductos.put("precio", producto.getPrecioProducto());
            mapaProductos.put("marca", producto.getMarcaProducto());
            mapaProductos.put("especificacionesTecnicas",
                    producto.getEspecificacionesTecnicas());
            mapaProductos.put("imagenProducto", producto.getImagenProducto());
            mapaProductos.put("disponibilidad", producto.isDisponibilidadProducto());
            mapaProductos.put("cantidad", producto.getCantidadProducto());
            mapaProductos.put("identificador", producto.getIdentificador());

            productosResponse.add(mapaProductos);
        }

        return productosResponse;
    }

    // Metodo para normalizar los textos que ponga el usuario y me busque sin tilde
    // los campos, TAMBIEN LO USO PARA TEXTOS TODOS SIN TILDE EN LA BASE DE DATOS

    private String normalizeText(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // Metodo para hacer una busqueda de minimo a maximo
    @Override
    public List<Map<String, Object>> buscarProductosPorRangoDePrecio(double precioMin, double precioMax,
            int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        try {
            Page<Producto> productosPage = productoRepositorio.findByPrecioProductoBetween(precioMin, precioMax,
                    pageable);

            List<Map<String, Object>> productosResponse = new ArrayList<>();

            for (Producto producto : productosPage.getContent()) {
                Map<String, Object> mapaProductos = new HashMap<>();
                mapaProductos.put("id", producto.get_id());
                mapaProductos.put("descripcion", producto.getDescripcionProducto());
                mapaProductos.put("categoria", producto.getCategoriaProducto());
                mapaProductos.put("nombre", producto.getNombreProducto());
                mapaProductos.put("precio", producto.getPrecioProducto());
                mapaProductos.put("marca", producto.getMarcaProducto());
                mapaProductos.put("especificacionesTecnicas",
                        producto.getEspecificacionesTecnicas());
                mapaProductos.put("imagenProducto", producto.getImagenProducto());
                mapaProductos.put("cantidad", producto.getCantidadProducto());
                mapaProductos.put("disponibilidad", producto.isDisponibilidadProducto());
                mapaProductos.put("identificador", producto.getIdentificador());

                productosResponse.add(mapaProductos);
            }
            return productosResponse;
        } catch (Exception e) {
            e.printStackTrace();
            // Mensaje de error, DEVUELVO LA LISTA VACÍA
            System.out.println("Error al buscar productos por rango de precio: " +
                    e.getMessage());
            return Collections.emptyList();
        }
    }

    @Override
    public String subirImagen(MultipartFile file, CrearProductoDTO crearProductoDTO) throws Exception {
        
        try {
            
            String fileName = UUID.randomUUID().toString();
            byte[] bytes = file.getBytes();
            String fileOriginalName = file.getOriginalFilename();

            long fileSize = file.getSize();
            long maxFile = 5 * 1024 * 1024; // 5 MB

            if (fileSize > maxFile) {
                throw new Exception("Error: El archivo es demasiado grande");
            }

            if (!fileOriginalName.endsWith(".jpg") && !fileOriginalName.endsWith(".png")
                    && !fileOriginalName.endsWith(".jpeg")) {
                throw new Exception("Error: El archivo debe ser JPG, PNG o JPEG");
            }

            String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf(".") + 1);
            String newFileName = fileName + "." + fileExtension;

            Path path = Paths.get("C:\\Users\\jrsm\\Desktop\\img\\" + newFileName);
            Files.write(path, bytes);

            return newFileName;

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Error al subir la imagen" + e.getMessage());
        }
    }

}
