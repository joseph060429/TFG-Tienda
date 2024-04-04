package com.proyecto.tienda.backend.service.PedidoServicio.AuthPedidoServicio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.UsuarioPedidoDTO;
import com.proyecto.tienda.backend.UtilEnum.EPedido;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.util.ResendUtil;

@Service
public class AuthPedidoServicioImpl implements AuthPedidoServicio {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private ResendUtil resend;

    // IMPLEMENTACION DEL METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A ENVIADO
    @Transactional
    @Override
    public ResponseEntity<?> actualizarEstadoPedidoEnviado(String _id, ActualizarPedidoDTO actualizarPedidoDTO) {

        // Busco el pedido por el Id
        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findBy_id(_id);

        if (pedidoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }

        try {

            // Busco el usuario por el email
            String email = pedidoOptional.get().getUsuario().getEmail();

            // Obtengo el pedido de la base de datos
            PedidosModelo pedido = pedidoOptional.get();

            // Verifico que el estado que ponga en el postman sea ENVIADO O enviado, SI PONE
            // OTRO ESTADO que no este en el enum
            // lanzare la exception que no un estado valido para esta operacion
            if (!EPedido.ENVIADO.equals(EPedido.valueOf(actualizarPedidoDTO.getEstado().toUpperCase()))) {
                System.out.println("ESTADO: " + EPedido.valueOf(actualizarPedidoDTO.getEstado()));
                return ResponseEntity.status(400).body("El pedido no tiene un estado válido para esta operación");
            }

            // Si el pedido ya tiene un estado valido que no sea ENVIADO, devuelvo un error
            if (EPedido.ENVIADO.toString().equals(pedido.getEstado())) {
                return ResponseEntity.status(400).body("El pedido ya ha sido enviado");
            }
            // Actualizo el estado del pedido
            pedido.setEstado(EPedido.ENVIADO.toString());

            // Calculo y seteo la fecha de envío y entrega estimada
            actualizarPedidoDTO.setFechaEnvioPedido();
            actualizarPedidoDTO.setFechaEntregaEstimada();

            // Seteo las fechas en el pedido
            pedido.setFechaEnvio(actualizarPedidoDTO.getFechaEnvio());
            pedido.setFechaEntregaEstimada(actualizarPedidoDTO.getFechaEntregaEstimada());

            // Seteo el trackingNumber
            int longitud = 10;
            String trackingNumber = generarTrykinmNumber(longitud);

            // Setear el trackingNumber en el objeto Pedido
            pedido.setTrackingNumber(trackingNumber);
            System.out.println("TRACKING: " + trackingNumber);

            // Envio el email al usuario
            resend.enviarEmailEnvioDelPedido(email, trackingNumber);

            // Guardo el pedido
            pedidoRepositorio.save(pedido);

            return ResponseEntity.ok("Se actualizo correctamente el pedido");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el pedido: has puesto un estado que no existe");
        }
    }




    // IMPLEMENTACION DEL METODO PARA BUSCAR EL PEDIDO POR ESTADOS
    @Override
    public ResponseEntity<List<PedidosModelo>> obtenerPedidosPorEstado(String estado, int page, int size) {
        try {
            EPedido estadoPedido = EPedido.valueOf(estado.trim().toUpperCase());

            Pageable pageable = PageRequest.of(page, size);

            Page<PedidosModelo> pedidosPage = pedidoRepositorio.findByEstado(estadoPedido.name(), pageable);

            // Mostrar solo los campos necesarios
            List<PedidosModelo> contenido = new ArrayList<>();
            for (PedidosModelo pedido : pedidosPage.getContent()) {
                // Actualizar los campos del pedido existente
                pedido.setFechaPedido(pedido.getFechaPedido());
                pedido.setEstado(pedido.getEstado());
                pedido.setNumPedido(pedido.getNumPedido());
                pedido.setProductos(pedido.getProductos());
                pedido.setTipoPago(pedido.getTipoPago());
                pedido.setDireccionEnvio(pedido.getDireccionEnvio());

                // Crear DTO de usuario y establecerlo en el pedido existente
                UsuarioModelo usuarioModelo = pedido.getUsuario();
                UsuarioPedidoDTO usuarioDTO = new UsuarioPedidoDTO();
                usuarioDTO.set_id(usuarioModelo.get_id());
                usuarioDTO.setNombre(usuarioModelo.getNombre());
                usuarioDTO.setApellido(usuarioModelo.getApellido());
                usuarioDTO.setEmail(usuarioModelo.getEmail());
                pedido.setUsuarioFromModelo(usuarioModelo);

                contenido.add(pedido);
            }

            return ResponseEntity.ok(contenido);
        } catch (IllegalArgumentException e) {
            System.err.println("Error: Estado no válido");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ArrayList<PedidosModelo>());
        }
    }

    // METODO PARA GENERAR UN TRYKING NUMBER ALATORIO DEL 1 AL 10
    private String generarTrykinmNumber(int longitud) {
        StringBuilder trackingNumber = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < longitud; i++) {
            // Generar un número aleatorio entre 0 y 9 (inclusive)
            int digitoAleatorio = random.nextInt(10);

            // Agregar el dígito al código de recuperación
            trackingNumber.append(digitoAleatorio);
        }

        return trackingNumber.toString();
    }

    // // prueba
    // private String carga(InputStream inputStream, String trackingNumber) throws IOException {

    //     try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
    //         StringBuilder stringBuilder = new StringBuilder();
    //         String line;

    //         while ((line = reader.readLine()) != null) {
    //             stringBuilder.append(line).append("\n");
    //         }

    //         // Reemplazo el marcador de posición con el código real de recuperación que
    //         // tengo en mi html
    //         return stringBuilder.toString().replace("{{codigoRecuperacion}}", trackingNumber);
    //     }
    // }



    // IMPLEMENTACION DEL METODO PARA PONER EL ESTADO DEL PEDIDO

}
