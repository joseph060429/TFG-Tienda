package com.proyecto.tienda.backend.service.ProductoServicio.AdminProductoServicio;

import java.io.FileInputStream;
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
import com.proyecto.tienda.backend.models.ProductoModelo;
import com.proyecto.tienda.backend.repositorios.ProductoRepositorio;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;

@Service
public class AdminProductoServicioImpl implements AdminProductoServicio {

    @Autowired
    private ProductoRepositorio productoRepositorio;

    // @Value("${directorio.imagenes.path}")
    // private String directorioImagenesPath;

    @Value("${firebase.credentials.path}")
    private String firebaseCredentialsPath;

    // IMPLEMENTACION DEL METODO PARA LA CONSTRUCCION DEL PRODUCTO
    private ResponseEntity<?> construirProducto(CrearProductoDTO crearProductoDTO, MultipartFile file) {

        ProductoModelo nuevoProducto = new ProductoModelo();

        try {
            // Para verificar la categoria
            EProducto categoriaProductoEnum = EProducto.valueOf(crearProductoDTO.getCategoriaProducto());
            nuevoProducto.setCategoriaProducto(categoriaProductoEnum);

            // Construyo los demas campos
            nuevoProducto.setNombreProducto(normalizeText(crearProductoDTO.getNombreProducto().trim().toUpperCase()));
            nuevoProducto.setDescripcionProducto(normalizeText(crearProductoDTO.getDescripcionProducto().trim()));
            nuevoProducto.setPrecioProducto(crearProductoDTO.getPrecioProducto());
            nuevoProducto.setDisponibilidadProducto(crearProductoDTO.getCantidadProducto() > 0);
            nuevoProducto.setCantidadProducto(crearProductoDTO.getCantidadProducto());
            nuevoProducto.setMarcaProducto(normalizeText(crearProductoDTO.getMarcaProducto().trim().toUpperCase()));
            nuevoProducto
                    .setEspecificacionesTecnicas(normalizeText(crearProductoDTO.getEspecificacionesTecnicas().trim()));

            // Construyo el identificador
            String identificador = construirIdentificador(crearProductoDTO);
            nuevoProducto.setIdentificador(normalizeText(identificador));

            // Subo la imagen
            ResponseEntity<String> responseImagen = subirImagen(file);
            if (responseImagen.getStatusCode().is2xxSuccessful()) {
                String nombreImagen = responseImagen.getBody();
                // String nombreSinExtension = nombreImagen.substring(0,
                // nombreImagen.lastIndexOf("."));
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

    // IMPLEMENTACION DEL METODO PARA CREAR PRODUCTO
    @Override
    public ResponseEntity<?> crearProducto(CrearProductoDTO crearProductoDTO, MultipartFile file) {
        try {

            ResponseEntity<?> construirProductoResponse = construirProducto(crearProductoDTO, file);

            // Verifico si la construcción del producto fue exitosa
            if (construirProductoResponse.getStatusCode().is2xxSuccessful()) {
                ProductoModelo nuevoProducto = (ProductoModelo) construirProductoResponse.getBody();

                // Valido si el identificador ya existe en la base de datos
                String identificador = nuevoProducto.getIdentificador();

                if (productoRepositorio.existsByIdentificador(identificador)) {
                    // // Elimino la imagen subida por el error asi no se me almacena en la carpeta
                    // imagenes, ya que hubo un error
                    String nombreImagen = nuevoProducto.getImagenProducto();

                    if (nombreImagen.endsWith(".jpg") || nombreImagen.endsWith(".jpng")
                            || nombreImagen.endsWith(".png") || nombreImagen.endsWith(".webp")) {
                        borrarImagen(nombreImagen);
                    }
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

    // IMPLEMENTACION DEL METODO PARA ELIMINAR EL PRODUCTO
    @Override
    public String eliminarProducto(String _id) {

        Optional<ProductoModelo> productOptional = productoRepositorio.findById(_id);

        if (productOptional.isPresent()) {
            ProductoModelo producto = productOptional.get();
            productoRepositorio.deleteBy_id(producto.get_id());

            // Eliminar la imagen de la carpeta imagen cuando el id existe en la base de
            // datos
            String nombreImagenActual = producto.getImagenProducto();
            if (nombreImagenActual != null && !nombreImagenActual.isEmpty()) {
                System.out.println("Eliminando imagen: " + nombreImagenActual);
                borrarImagen(nombreImagenActual);
            }
            return "Producto eliminado correctamente";
        } else {
            return "Producto no encontrado";
        }
    }

    // IDENTIFICADOR PARA NO INSERTAR EL MISMO PRODUCTO 2 VECES EN LA CREACIÓN
    private String construirIdentificador(CrearProductoDTO crearProductoDTO) {
        String categoria = crearProductoDTO.getCategoriaProducto().toLowerCase();
        String nombre = crearProductoDTO.getNombreProducto().toLowerCase().replaceAll("\\s+", "");
        String marca = crearProductoDTO.getMarcaProducto().toLowerCase().replaceAll("\\s+", "");

        return String.format("%s-%s-%s", categoria, nombre, marca);
    }

    // IMPLEMENTACION DEL METODO PARA ACTUALIZAR PRODUCTO
    @Override
    public ResponseEntity<?> actualizarProducto(String _id, ActualizarProductoDTO actualizarProductoDTO,
            MultipartFile file) {

                System.out.println("IMAGEN jakjajkakjakjajkajk: " + file);

        try {

            Optional<ProductoModelo> productOptional = productoRepositorio.findById(_id);

            if (productOptional.isPresent()) {
                // Obtengo el objeto de la base de datos
                ProductoModelo producto = productOptional.get();

                // Construyo el nuevo Identificador para comprobar luego si existe o no en mi
                // base de datos
                String identificadorActualizado = construirIdentificadorActualizado(actualizarProductoDTO, producto);

                // Verifico si al menos uno de los campos con los que se construye el
                // identificador(categoria, nombre, marca) NO es nulo
                if (actualizarProductoDTO.getCategoriaProducto() != null ||
                        actualizarProductoDTO.getNombreProducto() != null ||
                        actualizarProductoDTO.getMarcaProducto() != null) {

                    // Verifico si ya existe un producto en el repositorio con el mismo
                    // identificador
                    if (productoRepositorio.existsByIdentificador(identificadorActualizado)) {
                        // Si existe un producto con el mismo identificador, verifico si el producto
                        // encontrado es el mismo que estoy intentando actualizar
                        if (productoRepositorio.findByIdentificador(identificadorActualizado).get().get_id()
                                .equals(producto.get_id())) {
                            // Si el producto encontrado tiene el mismo _id que el producto que estoy
                            // intentando actualizar, no hago nada
                            // Esto significa que el producto que estoy intentando actualizar ya existe en
                            // el repositorio
                            // con el mismo identificador y no hay conflicto
                            
                        } else{
                            return ResponseEntity.status(400).body("Ya existe un producto con el mismo identificador");
                        }
                    }
                    producto.setIdentificador(identificadorActualizado);
                }
                // Si todo va bien osea si el identificador no existe en la base de datos, tanto
                // si he puesto los campos importantes o no actualizo la imagen
                System.out.println("IMAGEN: " + file);
                
                if (file != null) {
                    System.out.println("hola" + file);
                    ResponseEntity<String> responseImagen = subirImagen(file);
                    if (responseImagen.getStatusCode().is2xxSuccessful()) {
                        String nombreImagenNueva = responseImagen.getBody();

                        // Borrar la imagen anterior
                        String nombreImagenActual = producto.getImagenProducto();
                        if (nombreImagenActual != null && !nombreImagenActual.isEmpty()) {
                            borrarImagen(nombreImagenActual);
                        }

                        // Actualizo la imagen del producto con la nueva imagen subida
                        producto.setImagenProducto(nombreImagenNueva);
                    } else {
                        return ResponseEntity.badRequest()
                                .body("Error al construir el producto: " + responseImagen.getBody());
                    }
                }else{
                    System.out.println("ADIOS" +  file);
                }
                
                // Actualizo la categoria
                if (actualizarProductoDTO.getCategoriaProducto() != null) {
                    producto.setCategoriaProducto(EProducto.valueOf(actualizarProductoDTO.getCategoriaProducto()));
                    System.out.println("CATEGORIA: " + actualizarProductoDTO.getCategoriaProducto());
                }

                // Actualizo el nombre del producto
                if (actualizarProductoDTO.getNombreProducto() != null
                        && !actualizarProductoDTO.getNombreProducto().isEmpty()) {
                    producto.setNombreProducto(
                            normalizeText(actualizarProductoDTO.getNombreProducto().trim().toUpperCase()));
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
                    producto.setMarcaProducto(
                            normalizeText(actualizarProductoDTO.getMarcaProducto().trim().toUpperCase()));
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

                // Guardo el producto en la base de datos
                productoRepositorio.save(producto);
                return ResponseEntity.ok("Producto actualizado exitosamente");
            } else {
                return ResponseEntity.status(404).body("Producto no encontrado");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al ACTUALIZAR el producto: " + e.getMessage());
        }
    }


    // IDENTIFICADOR PARA NO INSERTAR EL MISMO PRODUCTO 2 VECES EN LA ACTUALIZACIÓN
    private String construirIdentificadorActualizado(ActualizarProductoDTO actualizarProductoDTO,
            ProductoModelo productoExistente) {

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

    // IMPLEMENTACION DEL METODO PARA LISTAR UN PRODUCTO POR ID
    @Override
    public ResponseEntity<?> listarUnProducto(String _id) {
        Optional<ProductoModelo> productOptional = productoRepositorio.findById(_id);
        if (productOptional.isPresent()) {
            // System.out.println("Producto: " + productOptional.get());
            return ResponseEntity.ok(productOptional.get());
        } else {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }
    }

    // IMPLEMENTACION DEL METODO PARA LISTAR TODOS LOS PRODUCTOS
    @Override
    public Page<ProductoModelo> listarProductosAdmin(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        System.out.println("Hay " + productoRepositorio.count() + " productos");
        return productoRepositorio.findAll(pageable);
    }

    // IMPLEMENTACION DEL METODO PARA LA BUSQUEDA POR CAMPOS IMPORTANTES Y MOSTRAR
    // TODOS LOS CAMPOS PARA EL ADMIN
    @Override
    public List<Map<String, Object>> buscarProductosAdmin(

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

        Page<ProductoModelo> productosPage;

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

        for (ProductoModelo producto : productosPage.getContent()) {
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

    // IMPLEMENTACION DEL MÉTODO PARA BUSCAR PRODUCTO POR CUALQUIER ESPECIFICACIÓN Y
    // QUE LE MUESTRE TODOS LOS CAMPOS AL ADMIN
    @Override
    public List<Map<String, Object>> buscarProductosPorEspecificacionAdmin(String especificacion, int page, int size) {
        // System.out.println("Especificación: " + especificacion);

        // Configuro la paginación
        Pageable pageable = PageRequest.of(page, size);

        // NormalizO la especificación
        String normalizedEspecificacion = normalizeText(especificacion);

        // UtilizO el método del repositorio con paginación
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
            mapaProductos.put("especificacionesTecnicas",
                    producto.getEspecificacionesTecnicas());
            mapaProductos.put("imagenProducto", producto.getImagenProducto());
            mapaProductos.put("disponibilidadProducto", producto.isDisponibilidadProducto());
            mapaProductos.put("cantidadProducto", producto.getCantidadProducto());
            mapaProductos.put("identificador", producto.getIdentificador());
            productosResponse.add(mapaProductos);
        }

        return productosResponse;
    }

    // IMPLEMENTACION DEL METODO PARA NORMALIZAR LOS TEXTOS QUE PONGA EL USUARIO Y
    // ME BUSQUE SIN TILDE
    // LOS CAMPOS, TAMBIÉN LO USO PARA INTRODUCIR TEXTOS SIN TILDE EN LA BASE DE
    // DATOS
    private String normalizeText(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    // IMPLEMENTACION DEL METODO PARA BUSCAR PRODUCTOS POR PRECIO, MAYOR A MENOR Y
    // VICEVERSA
    @Override
    public List<Map<String, Object>> buscarProductosPorRangoDePrecio(double precioMin, double precioMax,
            int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        try {
            Page<ProductoModelo> productosPage = productoRepositorio.findByPrecioProductoBetween(precioMin, precioMax,
                    pageable);

            List<Map<String, Object>> productosResponse = new ArrayList<>();

            for (ProductoModelo producto : productosPage.getContent()) {
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

    // IMPLEMENTACION DEL METODO PARA SUBIR IMAGENES
    public ResponseEntity<String> subirImagen(MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("La imagen no puede estar vacía");
            }

            String fileOriginalName = file.getOriginalFilename();
            String fileExtension = getFileExtension(fileOriginalName);
            String fileName = UUID.randomUUID().toString() + "." + fileExtension;

            long fileSize = file.getSize();
            long maxFile = 5 * 1024 * 1024; // 5 MB

            if (fileSize > maxFile) {
                return ResponseEntity.badRequest().body("La imagen es demasiado grande");
            }

            if (!fileExtension.equals("jpg") && !fileExtension.equals("png")
                    && !fileExtension.equals("jpeg") && !fileExtension.equals("webp")) {
                return ResponseEntity.badRequest().body("La imagen debe ser JPG, PNG, JPEG o WEBP");
            }

            // Cargar las credenciales de Firebase desde el archivo
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(firebaseCredentialsPath));
            System.out.println("Ruta del archivo de credenciales: " + credentials.toString());
            // Configurar la identificación del proyecto y la autenticación
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId("proyecto-ionic-tienda")
                    .build()
                    .getService();

            // Crear una referencia a la carpeta en Firebase Storage donde deseas almacenar
            // la imagen
            BlobId blobId = BlobId.of("proyecto-ionic-tienda.appspot.com", "Imagenes-Productos/" + fileName);
            BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                    .setContentType("image/" + fileExtension)
                    .build();

            // Subir el archivo a Firebase Storage
            storage.create(blobInfo, file.getBytes());

            return ResponseEntity.ok(fileName);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la imagen a Firebase Storage: " + e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        } else {
            return "";
        }
    }

    // IMPLEMENTACION DEL METODO PARA BORRAR IMAGEN
    public void borrarImagen(String nombreImagen) {
        try {
            // Cargar las credenciales de Firebase desde el archivo JSON
            GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(firebaseCredentialsPath));

            // Configurar la identificación del proyecto y la autenticación
            Storage storage = StorageOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId("proyecto-ionic-tienda")
                    .build()
                    .getService();

            // Construir el identificador del blob (imagen) que se desea borrar
            String bucketName = "proyecto-ionic-tienda.appspot.com";
            BlobId blobId = BlobId.of(bucketName, "Imagenes-Productos/" + nombreImagen);

            // Borrar la imagen del Firebase Storage
            boolean borradoExitoso = storage.delete(blobId);

            if (borradoExitoso) {
                System.out.println("Imagen eliminada correctamente de Firebase Storage: " + nombreImagen);
            } else {
                System.out.println("No se pudo eliminar la imagen de Firebase Storage: " + nombreImagen);
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al intentar eliminar la imagen de Firebase Storage: " + e.getMessage());
        }
    }

}
