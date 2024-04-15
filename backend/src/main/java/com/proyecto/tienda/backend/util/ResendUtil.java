package com.proyecto.tienda.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.Color;
import java.awt.Font;
import java.io.*;

import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;

import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.proyecto.tienda.backend.DTO.DTOPedido.FacturaDTO;
import com.proyecto.tienda.backend.DTO.DTOPedido.ProductoPedidoDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.RecuperarContraseniaDTO;
import com.proyecto.tienda.backend.models.PedidosModelo;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.PedidoRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.resend.Resend;
import com.resend.services.emails.model.SendEmailRequest;
import com.resend.services.emails.model.Tag;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class ResendUtil {

        @Autowired
        UsuarioRepositorio usuarioRepositorio;

        @Autowired
        PedidoRepositorio pedidoRepositorio;

        @Value("${envio.email.token}")
        private String envioEmailToken;

        // METODO DE ENVÍO DE EMAIL DE RECUPERACIÓN
        public void enviarEmailDeRecuperacion(String destinatarioEmail) {

                // Configurar el envío del correo con Resend
                Resend resend = new Resend(envioEmailToken);

                try (
                                InputStream htmlStream = getClass().getClassLoader()
                                                .getResourceAsStream("util/CuerpoGmailRecuperacion.html")) {
                        String codigoRecuperacion = generarCodigoRecuperacion(10);
                        String messageSubject = "Recuperación de contraseña";
                        String bodyText = cargarContenidoHtml(htmlStream, codigoRecuperacion, "", "");

                        // Busco el usuario por el email
                        Optional<UsuarioModelo> usuarioModelo = usuarioRepositorio.findByEmail(destinatarioEmail);

                        // Si el usuario existe
                        if (usuarioModelo.isPresent()) {
                                UsuarioModelo usuario = usuarioModelo.get();
                                // Establezco el nuevo codigo de recuperación en mi base de datos
                                usuario.setRecuperarContrasenia(codigoRecuperacion);

                                // Establezco la fecha de expiracion que puse que tenia 3 minutos
                                RecuperarContraseniaDTO recuperarContraseniaDTO = new RecuperarContraseniaDTO();
                                recuperarContraseniaDTO.setFechaExpiracion();
                                usuario.setExpiracionRecuperarContrasenia(
                                                recuperarContraseniaDTO.getExpiracionRecuperarContrasenia());
                                usuarioRepositorio.save(usuario);
                        }

                        // Cuando quiera enviar un archivo adjunto
                        // Attachment att = Attachment.builder()
                        // .fileName("CuerpoGmailRecuperacion.html")
                        // .content(bodyText)
                        // .build();

                        Tag tag = Tag.builder()
                                        .name("category")
                                        .value("confirm_email")
                                        .build();

                        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                                        .from("administracion@jastoredcomponents.es")
                                        .to(destinatarioEmail)
                                        .html(bodyText)
                                        // .attachments(List.of(att))
                                        .text(bodyText)
                                        .subject(messageSubject)
                                        .headers(Map.of(
                                                        "X-Entity-Ref-ID", "123456789"))
                                        .tags(tag)
                                        .build();

                        // Enviar el correo electrónico
                        resend.emails().send(sendEmailRequest);
                        System.out.println("Correo de recuperación enviado exitosamente. " + codigoRecuperacion);
                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        // GENERO MI CÓDIGO DE RECUPERACIÓN ALEATORIO CON NÚMEROS Y LETRAS
        private String generarCodigoRecuperacion(int longitud) {

                StringBuilder codigoRecuperacion = new StringBuilder();
                Random random = new Random();

                for (int i = 0; i < longitud; i++) {
                        // Generar un número aleatorio entre 0 y 35
                        int caracterAleatorio = random.nextInt(36);

                        // Convertir el número a un carácter
                        char caracter;
                        if (caracterAleatorio < 10) {
                                caracter = (char) ('0' + caracterAleatorio);
                        } else {
                                caracter = (char) ('A' + (caracterAleatorio - 10));
                        }

                        // Agregar el carácter al código de recuperación
                        codigoRecuperacion.append(caracter);
                }

                return codigoRecuperacion.toString();
        }

        // MÉTODO ES PARA LEER LA PLANTILLA HTML
        private String cargarContenidoHtml(InputStream inputStream, String codigoRecuperacion, String trackingNumber,
                        String _idPedido)
                        throws IOException {
                try (BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;

                        while ((line = reader.readLine()) != null) {
                                // Reemplazar el marcador de posición con el código de recuperación real
                                line = line.replace("{{codigoRecuperacion}}", codigoRecuperacion);
                                // Reemplazar el marcador de posición con el número de seguimiento real
                                line = line.replace("{{trackingNumber}}", trackingNumber);

                                // Reemplazar el marcador de dirección con el id del pedido real
                                line = line.replace("{{_idPedido}}", _idPedido);

                                stringBuilder.append(line).append("\n");
                        }

                        return stringBuilder.toString();
                }
        }

        // METODO DE ENVÍO DE EMAIL INFORMANDO DE UN RETRASO EN EL PEDIDO
        public void enviarEmailDeRetrasoDelPedido(String destinatarioEmail) {

                // Configurar el envío del correo con Resend
                Resend resend = new Resend(envioEmailToken);

                try (
                                InputStream htmlStream = getClass().getClassLoader()
                                                .getResourceAsStream("util/CuerpoGmailDisculpaRetrasoPedido.html")) {

                        String messageSubject = "Importante: Retraso en el Envío de tu Pedido";
                        String bodyText = cargarContenidoHtml(htmlStream, "", "", "");

                        Tag tag = Tag.builder()
                                        .name("category")
                                        .value("retraso_pedido")
                                        .build();

                        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                                        .from("administracion@jastoredcomponents.es")
                                        .to(destinatarioEmail)
                                        .html(bodyText)
                                        .subject(messageSubject)
                                        .headers(Map.of(
                                                        "X-Entity-Ref-ID", "123456789"))
                                        .tags(tag)
                                        .build();

                        // Enviar el correo electrónico
                        resend.emails().send(sendEmailRequest);

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        // METODO DE ENVÍO DE EMAIL INFORMANDO DEL ENVIO DEL PEDIDO
        public void enviarEmailEnvioDelPedido(String destinatarioEmail, String trackingNumber) {

                // Configurar el envío del correo con Resend
                Resend resend = new Resend(envioEmailToken);

                try (
                                InputStream htmlStream = getClass().getClassLoader()
                                                .getResourceAsStream("util/CuerpoGmailEnvioPedido.html")) {

                        String messageSubject = "¡Tu Pedido ha sido Enviado!";
                        String bodyText = cargarContenidoHtml(htmlStream, "", trackingNumber, "");

                        Tag tag = Tag.builder()
                                        .name("category")
                                        .value("envio_pedido")
                                        .build();

                        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                                        .from("administracion@jastoredcomponents.es")
                                        .to(destinatarioEmail)
                                        .html(bodyText)
                                        .subject(messageSubject)
                                        .headers(Map.of(
                                                        "X-Entity-Ref-ID", "123456789"))
                                        .tags(tag)
                                        .build();

                        // Enviar el correo electrónico
                        resend.emails().send(sendEmailRequest);

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        // PROBANDO

        // public void enviarEmailEnvioDelPedido(String destinatarioEmail, String
        // trackingNumber, FacturaDTO facturaDTO) {

        // try {
        // // Crear un flujo de bytes para almacenar el PDF en memoria
        // ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();

        // // Generar la factura en PDF en memoria
        // generarFacturaPDF(facturaDTO, pdfStream);

        // // Configurar el envío del correo con Resend
        // Resend resend = new Resend(envioEmailToken);

        // try (
        // InputStream htmlStream = getClass().getClassLoader()
        // .getResourceAsStream("util/CuerpoGmailEnvioPedido.html")) {

        // String messageSubject = "¡Tu Pedido ha sido Enviado!";
        // String bodyText = cargarContenidoHtml(htmlStream, "", trackingNumber, "");

        // Tag tag = Tag.builder()
        // .name("category")
        // .value("envio_pedido")
        // .build();

        // // Adjuntar el PDF al correo electrónico
        // SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
        // .from("administracion@jastoredcomponents.es")
        // .to(destinatarioEmail)
        // .html(bodyText)
        // .subject(messageSubject)
        // .headers(Map.of(
        // "X-Entity-Ref-ID", "123456789"))
        // .tags(tag)
        // .attachments(Map.of("factura.pdf", pdfStream.toByteArray()))
        // .build();

        // // Enviar el correo electrónico
        // resend.emails().send(sendEmailRequest);

        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        // } catch (Exception e) {
        // e.printStackTrace();
        // System.out.println("Error al generar la factura: " + e.getMessage());
        // }
        // }

        // METODO DE ENVÍO DE EMAIL INFORMANDO AL USUARIO QUE LA DIRECCION ES ERRONEA
        public void enviarEmailDireccionErronea(String destinatarioEmail, String id_pedido) {

                System.out.println("destinantario desde resend " + destinatarioEmail);

                System.out.println("ID PEDIDO desde resend: " + id_pedido);

                Resend resend = new Resend(envioEmailToken);

                try (
                                InputStream htmlStream = getClass().getClassLoader()
                                                .getResourceAsStream("util/CuerpoGmailDireccionErronea.html")) {

                        String messageSubject = "Importante: DIRECCIÓN ERRONEA EN TU PEDIDO";
                        String bodyText = cargarContenidoHtml(htmlStream, "", "", id_pedido);
                        System.out.println("ID DEL PEDIDO DESDE RESEND 2 " + id_pedido);

                        Tag tag = Tag.builder()
                                        .name("category")
                                        .value("envio_pedido")
                                        .build();

                        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                                        .from("administracion@jastoredcomponents.es")
                                        .to(destinatarioEmail)
                                        .html(bodyText)
                                        .subject(messageSubject)
                                        .headers(Map.of(
                                                        "X-Entity-Ref-ID", "123456789"))
                                        .tags(tag)
                                        .build();

                        // Enviar el correo electrónico
                        resend.emails().send(sendEmailRequest);

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        // METODO DE ENVÍO DE EMAIL INFORMANDO QUE NO HUBO NADIE EN CASA O QUE NO
        // RESPONDIO A LA LLAMADA DEL REPARTIDOR
        public void enviarEmailNadieEnCasa(String destinatarioEmail) {

                // Configurar el envío del correo con Resend
                Resend resend = new Resend(envioEmailToken);

                try (
                                InputStream htmlStream = getClass().getClassLoader()
                                                .getResourceAsStream("util/CuerpoGmailNoHuboNadieEnCasa.html")) {

                        String messageSubject = "Importante! Problema con la Entrega de tu Pedido";
                        String bodyText = cargarContenidoHtml(htmlStream, "", "", "");

                        Tag tag = Tag.builder()
                                        .name("category")
                                        .value("retraso_pedido")
                                        .build();

                        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                                        .from("administracion@jastoredcomponents.es")
                                        .to(destinatarioEmail)
                                        .html(bodyText)
                                        .subject(messageSubject)
                                        .headers(Map.of(
                                                        "X-Entity-Ref-ID", "123456789"))
                                        .tags(tag)
                                        .build();

                        // Enviar el correo electrónico
                        resend.emails().send(sendEmailRequest);

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

        // METODO DE ENVÍO DE EMAIL INFORMANDO QUE NO HUBO NADIE EN CASA O QUE NO
        // RESPONDIO A LA LLAMADA DEL REPARTIDOR
        public void enviarEmailPedidoCanceladoUsuario(String destinatarioEmail) {

                // Configurar el envío del correo con Resend
                Resend resend = new Resend(envioEmailToken);

                try (
                                InputStream htmlStream = getClass().getClassLoader()
                                                .getResourceAsStream("util/CuerpoGmailPedidoCanceladoUsuario.html")) {

                        String messageSubject = "¡Importante! Confirmación de Cancelación de tu Pedido";
                        String bodyText = cargarContenidoHtml(htmlStream, "", "", "");

                        Tag tag = Tag.builder()
                                        .name("category")
                                        .value("retraso_pedido")
                                        .build();

                        SendEmailRequest sendEmailRequest = SendEmailRequest.builder()
                                        .from("administracion@jastoredcomponents.es")
                                        .to(destinatarioEmail)
                                        .html(bodyText)
                                        .subject(messageSubject)
                                        .headers(Map.of(
                                                        "X-Entity-Ref-ID", "123456789"))
                                        .tags(tag)
                                        .build();

                        // Enviar el correo electrónico
                        resend.emails().send(sendEmailRequest);

                } catch (Exception e) {
                        e.printStackTrace();
                }
        }

}
