package com.proyecto.tienda.backend.service.ProductoServicio.AuthProductoServicio;

import java.io.IOException;
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
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${directorio.imagenes.path}")
    private String directorioImagenesPath;

    // Construccion del producto
    private ResponseEntity<?> construirProducto(CrearProductoDTO crearProductoDTO, MultipartFile file) {

        Producto nuevoProducto = new Producto();

        try {
            // Para verificar la categoria
            EProducto categoriaProductoEnum = EProducto.valueOf(crearProductoDTO.getCategoriaProducto());
            nuevoProducto.setCategoriaProducto(categoriaProductoEnum);

            // Construyo los demas campos
            nuevoProducto.setNombreProducto(normalizeText(crearProductoDTO.getNombreProducto().trim()));
            nuevoProducto.setDescripcionProducto(normalizeText(crearProductoDTO.getDescripcionProducto().trim()));
            nuevoProducto.setPrecioProducto(crearProductoDTO.getPrecioProducto());
            nuevoProducto.setDisponibilidadProducto(crearProductoDTO.getCantidadProducto() > 0);
            nuevoProducto.setCantidadProducto(crearProductoDTO.getCantidadProducto());
            nuevoProducto.setMarcaProducto(normalizeText(crearProductoDTO.getMarcaProducto().trim()));
            nuevoProducto
                    .setEspecificacionesTecnicas(normalizeText(crearProductoDTO.getEspecificacionesTecnicas().trim()));

            // Construyo el identificador
            String identificador = construirIdentificador(crearProductoDTO);
            nuevoProducto.setIdentificador(normalizeText(identificador));

            // Subo la imagen
            ResponseEntity<String> responseImagen = subirImagen(file);
            if (responseImagen.getStatusCode().is2xxSuccessful()) {
                String nombreImagen = responseImagen.getBody();
                nuevoProducto.setImagenProducto(nombreImagen);
            } else {
                return ResponseEntity.badRequest().body("Error al construir el producto: " + responseImagen.getBody());
            }

            if (nuevoProducto.get_id() == null) {
                nuevoProducto.set_id(UUID.randomUUID().toString());
            }

            return ResponseEntity.ok(nuevoProducto);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al construir el producto: " + e.getMessage());
        }

    }

    // Crear Producto
    @Override
    public ResponseEntity<?> crearProducto(CrearProductoDTO crearProductoDTO, MultipartFile file) {
        try {

            ResponseEntity<?> construirProductoResponse = construirProducto(crearProductoDTO, file);

            // Verifico si la construcción del producto fue exitosa
            if (construirProductoResponse.getStatusCode().is2xxSuccessful()) {
                Producto nuevoProducto = (Producto) construirProductoResponse.getBody();

                // Valido si el identificador ya existe en la base de datos
                String identificador = nuevoProducto.getIdentificador();

                if (productoRepositorio.existsByIdentificador(identificador)) {
                    // Elimino la imagen subida por el error
                    borrarImagen(nuevoProducto.getImagenProducto());
                    return ResponseEntity.status(400).body("Ya existe un producto con el mismo identificador");
                } else {
                    // Guardo el producto en la base de datos
                    productoRepositorio.save(nuevoProducto);
                    return ResponseEntity.ok("Producto creado exitosamente");
                }
            } else {
                return construirProductoResponse;
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

    // Identificador para no insertar el mismo producto 2 veces en la creacion
    private String construirIdentificador(CrearProductoDTO crearProductoDTO) {
        String categoria = crearProductoDTO.getCategoriaProducto().toLowerCase();
        String nombre = crearProductoDTO.getNombreProducto().toLowerCase().replaceAll("\\s+", "");
        String marca = crearProductoDTO.getMarcaProducto().toLowerCase().replaceAll("\\s+", "");

        return String.format("%s-%s-%s", categoria, nombre, marca);
    }

    // Metodo para actualizar
    @Override
    public ResponseEntity<?> actualizarProducto(String _id, ActualizarProductoDTO actualizarProductoDTO,
            MultipartFile file) {

        try {

            Optional<Producto> productOptional = productoRepositorio.findById(_id);

            if (productOptional.isPresent()) {
                // Obtengo el objeto de la base de datos
                Producto producto = productOptional.get();

                // Actualizo la categoria
                if (actualizarProductoDTO.getCategoriaProducto() != null) {
                    producto.setCategoriaProducto(EProducto.valueOf(actualizarProductoDTO.getCategoriaProducto()));
                }

                // Actualizo el nombre del producto
                if (actualizarProductoDTO.getNombreProducto() != null
                        && !actualizarProductoDTO.getNombreProducto().isEmpty()) {
                    producto.setNombreProducto(normalizeText(actualizarProductoDTO.getNombreProducto().trim()));
                }

                // Actualizo la descripcion del producto
                if (actualizarProductoDTO.getDescripcionProducto() != null
                        && !actualizarProductoDTO.getDescripcionProducto().isEmpty()) {
                    producto.setDescripcionProducto(
                            normalizeText(actualizarProductoDTO.getDescripcionProducto().trim()));

                }
                // Actualizo la marca del producto
                if (actualizarProductoDTO.getMarcaProducto() != null
                        && !actualizarProductoDTO.getMarcaProducto().isEmpty()) {
                    producto.setMarcaProducto(normalizeText(actualizarProductoDTO.getMarcaProducto().trim()));
                }

                // Actualizo el precio del producto
                if (actualizarProductoDTO.getPrecioProducto() != null) {
                    if (actualizarProductoDTO.getPrecioProducto() > 0) {
                        producto.setPrecioProducto(actualizarProductoDTO.getPrecioProducto());
                    }
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
                if (actualizarProductoDTO.getEspecificacionesTecnicas() != null
                        && !actualizarProductoDTO.getEspecificacionesTecnicas().isEmpty()) {
                    producto.setEspecificacionesTecnicas(
                            normalizeText(actualizarProductoDTO.getEspecificacionesTecnicas().trim()));
                }

                // Actualizo la imagen del producto
                if (file != null) {
                    ResponseEntity<String> responseImagen = subirImagen(file);
                    if (responseImagen.getStatusCode().is2xxSuccessful()) {
                        String nombreImagen = responseImagen.getBody();
                        producto.setImagenProducto(nombreImagen);
                    } else {
                        return ResponseEntity.badRequest()
                                .body("Error al construir el producto: " + responseImagen.getBody());
                    }
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
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ACTUALIZAR el producto: " + e.getMessage());
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
    // los campos, TAMBIEN LO USO PARA introducir TEXTOS SIN TILDE EN LA BASE DE
    // DATOS
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

    // Metodo para subir imagenes
    public ResponseEntity<String> subirImagen(MultipartFile file) {
        try {

            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("La imágen no puede estar vacía");
            }

            String fileName = UUID.randomUUID().toString();
            byte[] bytes = file.getBytes();
            String fileOriginalName = file.getOriginalFilename();

            long fileSize = file.getSize();
            long maxFile = 5 * 1024 * 1024; // 5 MB

            if (fileSize > maxFile) {
                return ResponseEntity.badRequest().body("La imágen es demasiado grande");
            }

            if (!fileOriginalName.endsWith(".jpg") && !fileOriginalName.endsWith(".png")
                    && !fileOriginalName.endsWith(".jpeg")) {
                return ResponseEntity.badRequest().body("La imágen debe ser JPG, PNG o JPEG");
            }

            String fileExtension = fileOriginalName.substring(fileOriginalName.lastIndexOf(".") + 1);
            String newFileName = fileName + "." + fileExtension;

            // Viene del path
            Path path = Paths.get(directorioImagenesPath + newFileName);
            Files.write(path, bytes);

            return ResponseEntity.ok(newFileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la imagen: " + e.getMessage());
        }
    }

    // Metodo para eliminar una imagen de la carpeta imagenes
    private void borrarImagen(String nombreImagen) {
        try {
            Path path = Paths.get(directorioImagenesPath + nombreImagen);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al intentar eliminar la imagen: " + e.getMessage());
        }
    }

}
