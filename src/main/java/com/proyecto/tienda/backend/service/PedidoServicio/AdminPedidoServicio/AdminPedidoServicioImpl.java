package com.proyecto.tienda.backend.service.PedidoServicio.AdminPedidoServicio;

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
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarDireccionEnvioDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ActualizarPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.UsuarioPedidoDTO;
import com.proyecto.tienda.backend.UtilEnum.EPedido;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.util.ResendUtil;

@Service
public class AdminPedidoServicioImpl implements AdminPedidoServicio {

    @Autowired
    private PedidoRepositorio pedidoRepositorio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private ResendUtil resend;

    // IMPLEMENTACION DEL METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A ENVIADO
    @Transactional
    @Override
    public ResponseEntity<?> actualizarEstadoPedidoEnviado(String _id, String estado,
            ActualizarPedidoDTO actualizarPedidoDTO) {

        // Busco el pedido por el Id
        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findBy_id(_id);

        if (pedidoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }

        try {

            // Me traigo el email del usuario
            String email = pedidoOptional.get().getUsuario().getEmail();

            // Obtengo el pedido de la base de datos
            PedidosModelo pedido = pedidoOptional.get();

            // Verifico que el estado que ponga en el postman sea ENVIADO O enviado, SI PONE
            // OTRO ESTADO que no este en el enum
            // lanzare la exception que no un estado valido para esta operacion
            String estadoPedido = actualizarPedidoDTO.getEstado().toUpperCase().trim();
            System.out.println("ESTADO PEDIDO: " + estadoPedido);
            if (!EPedido.ENVIADO.toString().equalsIgnoreCase(estadoPedido)) {
                System.out.println("ESTADO: " + estadoPedido);
                return ResponseEntity.status(400).body("El pedido no tiene un estado válido para esta operación");
            }

            // Si el pedido ya tiene un estado valido que no sea ENVIADO, devuelvo un error
            if (EPedido.ENVIADO.toString().equals(pedido.getEstado().trim())) {
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

            // Añado una longitud de 10 caracteres al trackingNumber
            int longitud = 10;
            String trackingNumber = generarTrykinmNumber(longitud);

            // Seteo el trackingNumber en el objeto Pedido
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

            // Muestro solo lo que yo quiero de pedidos
            List<PedidosModelo> contenido = new ArrayList<>();
            for (PedidosModelo pedido : pedidosPage.getContent()) {
                // Actualizo los campos del pedido existente
                pedido.setFechaPedido(pedido.getFechaPedido());
                pedido.setEstado(pedido.getEstado());
                pedido.setNumPedido(pedido.getNumPedido());
                pedido.setProductos(pedido.getProductos());
                pedido.setTipoPago(pedido.getTipoPago());
                pedido.setDireccionCompletaEnvio(pedido.getDireccionCompletaEnvio());

                // Creo el DTO de usuarioPedido y establezco en el pedido existente
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

    // IMPLEMENTACION DEL METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A ENTREGADO,
    // LO HARÉ MANUALMENTE CUANDO EL SUPUESTO REPARTIDOR ME ENTRGUE TODOS LOS
    // PRODUCTOS (TANTO ENTREGADOS COMO NO ENTREGADOS)
    @Override
    public ResponseEntity<?> actualizarEstadoPedidoEntregado(String _id, ActualizarPedidoDTO actualizarPedidoDTO) {
        // Busco el pedido por el Id
        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findBy_id(_id);

        // Verfico que el pedido exista
        if (pedidoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }

        try {

            // Traigo el pedido de la base de datos
            PedidosModelo pedido = pedidoOptional.get();

            // Verifico que el estado que ponga en el postman sea ENTREGADO O entregado,
            String estadoPedido = actualizarPedidoDTO.getEstado().toUpperCase().trim();
            System.out.println("ESTADO PEDIDO: " + estadoPedido);
            if (!EPedido.ENTREGADO.toString().equalsIgnoreCase(estadoPedido)) {
                System.out.println("ESTADO: " + estadoPedido);
                return ResponseEntity.status(400).body("El pedido no tiene un estado válido para esta operación");
            }

            if (EPedido.ENTREGADO.toString().equals(pedido.getEstado().trim())) {
                return ResponseEntity.status(400).body("El pedido ya ha sido marcado como entregado");
            }

            pedido.setEstado(EPedido.ENTREGADO.toString());
            pedidoRepositorio.save(pedido);

            return ResponseEntity.ok("Se actualizo correctamente el pedido");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el pedido: has puesto un estado que no existe");
        }
    }

    // IMPLEMENTACION DEL METODO PARA BUSCAR PEDIDOS POR ID, POR QUE SI EL
    // REPARTIDOR ME VIENE CON EL PAQUETE DE VUELTA AL ALMACEN LE PREGUNTO EL MOTIVO
    // POR EL
    // CUAL NO RECIBIÓ EL PAQUETE EL USUARIO, SI ME DICE QUE FUE PORQUE LA DIRECCION
    // NO EXISTE LE ENVIO UN CORREO AL USUARIO DICIENDOLE QUE RESPONDA ESTE CORREO
    // CON UNA DIRECCION VALIDA PORQUE ESA DIRECCION NO EXISTE, SI EL REPARTIDOR ME
    // DICE QUE NO HABIA NADIE EN
    // CASA Y QUE LLAMO Y NADIE RESPONDIO LE ENVIO UN EMAIL AL USUARIO DICIENDOLE
    // QUE SU PEDIDO SE VOLVERA A ENVIAR EN UNOS DIAS YA QUE CUANDO EL REPARTIDOR
    // FUE NO HABIA NADIE EN CASA Y TAMPOCO LE RESPONDIERON AL TELEFONO
    public PedidosModelo listarUnPedido(String _id) {

        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findById(_id);

        if (pedidoOptional.isPresent()) {
            PedidosModelo pedido = pedidoOptional.get();
            UsuarioModelo usuarioModelo = pedido.getUsuario();

            // Convertir UsuarioModelo a UsuarioPedidoDTO
            UsuarioPedidoDTO usuarioPedidoDTO = new UsuarioPedidoDTO();
            usuarioPedidoDTO.set_id(usuarioModelo.get_id());
            usuarioPedidoDTO.setNombre(usuarioModelo.getNombre());
            usuarioPedidoDTO.setApellido(usuarioModelo.getApellido());
            usuarioPedidoDTO.setEmail(usuarioModelo.getEmail());

            // Asignar el DTO de usuario al pedido
            pedido.setUsuarioPedido(usuarioPedidoDTO);

            return pedido;
        } else {
            return null;
        }
    }

    // METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO A
    // PENDIENTE_CONFIRMACION_DIRECCION CUANDO EL REPARTIDOR LLEGA Y NO EXISTE ESA
    // DIRECCION
    @Transactional
    @Override
    public ResponseEntity<?> actualizarEstadoPedidoPendienteConfirmacionDireccion(String _id, String estado,
            ActualizarPedidoDTO actualizarPedidoDTO) {

        // Busco el pedido por el Id
        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findBy_id(_id);
        System.out.println("PEDIDO: " + pedidoOptional);

        if (pedidoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }

        try {
            // Obtengo el pedido de la base de datos
            PedidosModelo pedido = pedidoOptional.get();

            // Verifico que el estado que ponga en el postman sea
            // PENDIENTE_CONFIRMACION_DIRECCION o pendiente_confirmacion, SI PONE
            // OTRO ESTADO que no este en el enum
            // lanzare la exception que no un estado valido para esta operacion
            String estadoPedido = actualizarPedidoDTO.getEstado().toUpperCase().trim();

            if (!EPedido.PENDIENTE_CONFIRMACION_DIRECCION.toString().equalsIgnoreCase(estadoPedido)) {
                System.out.println("ESTADO: " + estadoPedido);
                return ResponseEntity.status(400).body("El pedido no tiene un estado válido para esta operación");
            }

            if (EPedido.PENDIENTE_CONFIRMACION_DIRECCION.toString().equals(pedido.getEstado().trim())) {
                return ResponseEntity.status(400)
                        .body("El pedido ya ha sido marcado como pendiente de confirmación de dirección");
            }

            pedido.setEstado(EPedido.PENDIENTE_CONFIRMACION_DIRECCION.toString());
            pedidoRepositorio.save(pedido);

            // Obtengo el email del usuario del pedido para enviar el email
            String email = pedido.getUsuario().getEmail();

            // Obtengo el id del pedido para decirle que su pedido con identificador
            // _id_pedido no ha podido ser entregado porque la direccion es invalida
            String id_pedido = pedido.get_id();

            // Envio el email al usuario
            resend.enviarEmailDireccionErronea(email, id_pedido);

            return ResponseEntity.ok("Se actualizó correctamente el pedido");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el pedido: has puesto un estado que no existe");
        }
    }

    // IMPLEMENTACION DEL METODO PARA ACTUALIZARLE LA DIRECCION AL USUARIO CON LOS
    // NUEVOS DATOS QUE ME ENVIO POR CORREO
    @Transactional
    @Override
    public ResponseEntity<?> actualizarDireccionEnvioAdmin(String _idPedido,
            ActualizarDireccionEnvioDTO actualizarDireccionEnvioDTO, UsuarioModelo usuario) {

        // Busco el pedido por id
        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findBy_id(_idPedido);

        if (pedidoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }

        try {

            // Obtengo el pedido de la base de datos para actualizale la direccion
            PedidosModelo pedidoModelo = pedidoOptional.get();

            // Obtengo el email del usuario
            String emailUsuario = pedidoModelo.getUsuario().getEmail();

            // Busco el usuario por email:
            Optional<UsuarioModelo> usuarioOptional = usuarioRepositorio.findByEmail(emailUsuario);

            // Obtengo el usuario de la base de datos
            UsuarioModelo usuarioBD = usuarioOptional.get();

            // Obtengo las direcciones de envío de ese usuario
            List<String> direccionesUsuario = usuarioBD.getDireccionesEnvio();
            System.out.println("DIRECCIONES ENVIO USUARIO " + direccionesUsuario);

            // Obtengo los datos del DTO
            String codigoPostal = actualizarDireccionEnvioDTO.getCodigoPostal().trim();
            String direccion = actualizarDireccionEnvioDTO.getDireccion().trim();
            String provincia = actualizarDireccionEnvioDTO.getProvincia();
            String numero = actualizarDireccionEnvioDTO.getNumero();
            String piso = actualizarDireccionEnvioDTO.getPiso();
            String puerta = actualizarDireccionEnvioDTO.getPuerta();

            // Agrego la dirección de envío al usuario con el metodo de agrgar direccion
            // completa que tengo en el modelo
            String direccionEnvio = usuarioBD.construirDireccionCompleta(direccion, provincia, puerta,
                    codigoPostal, piso,
                    numero);
            System.out.println("DIRECCION ENVIO " + direccionEnvio);

            usuarioRepositorio.save(usuarioBD);

            // Actualizo el pedido con la nueva dirección de envío
            pedidoModelo.setDireccionCompletaEnvio(direccionEnvio);
            // Guardo el pedido en la base de datos
            pedidoRepositorio.save(pedidoModelo);

            return ResponseEntity
                    .ok("Dirección de envío actualizada correctamente para el pedido con ID: " + _idPedido);

        } catch (IllegalArgumentException e) {
            System.out.println("Error al añadir la dirección de envío: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Error al añadir la dirección de envío: " + e.getMessage());
        }
    }


    // IMPLEMENTACION DEL METODO PARA ACTUALIZAR EL ESTADO DEL PEDIDO REPROGRAMADO_PARA_ENTREGA CUANDO EL REPARTIDOR LLEGA Y NO HAY NADIE EN CASA O NO RESPONDEN AL MÓVIL
    @Override
    public ResponseEntity<?> actualizarEstadoReprogramadoParaEntrega(String _id,
            ActualizarPedidoDTO actualizarPedidoDTO) {
        // Busco el pedido por el Id
        Optional<PedidosModelo> pedidoOptional = pedidoRepositorio.findBy_id(_id);

        if (pedidoOptional.isEmpty()) {
            return ResponseEntity.status(404).body("No se encontró el pedido");
        }
        try {
            // Obtengo el pedido de la base de datos
            PedidosModelo pedido = pedidoOptional.get();

            // Verifico que el estado que ponga en el postman sea
            // REPROGRAMADO_PARA_ENTREGA o reprogramado_para_entrega, SI PONE
            // OTRO ESTADO que no este en el enum
            // lanzare la exception que no un estado valido para esta operacion
            String estadoPedido = actualizarPedidoDTO.getEstado().toUpperCase().trim();

            if (!EPedido.REPROGRAMADO_PARA_ENTREGA.toString().equalsIgnoreCase(estadoPedido)) {
                System.out.println("ESTADO: " + estadoPedido);
                return ResponseEntity.status(400).body("El pedido no tiene un estado válido para esta operación");
            }

            if (EPedido.REPROGRAMADO_PARA_ENTREGA.toString().equals(pedido.getEstado().trim())) {
                return ResponseEntity.status(400)
                        .body("El pedido ya ha sido marcado como reprogramado para entrega");
            }

            pedido.setEstado(EPedido.REPROGRAMADO_PARA_ENTREGA.toString());
            pedidoRepositorio.save(pedido);

            // Obtengo el email del usuario del pedido para enviar el email
            String email = pedido.getUsuario().getEmail();

            // Envio el email al usuario
            resend.enviarEmailNadieEnCasa(email);

            return ResponseEntity.ok("Se actualizó correctamente el pedido");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error al actualizar el pedido: has puesto un estado que no existe");
        }

    }

}

// IMPLEMENTACION DEL METODO PARA PONER EL ESTADO DEL PEDIDO SWITCH CASE
