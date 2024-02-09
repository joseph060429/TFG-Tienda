package com.proyecto.tienda.backend.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.*;

import com.proyecto.tienda.backend.DTO.DTOUsuario.RecuperarContraseniaDTO;
import com.proyecto.tienda.backend.models.UsuarioModelo;
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

    @Value("${envio.email.token}")
    private String envioEmailToken;

    // @Autowired
    // private Resend resend;

    // Envio de email de recuperacion
    public void enviarEmailDeRecuperacion(String destinatarioEmail) {

        // Configurar el envío del correo con Resend
        Resend resend = new Resend(envioEmailToken);

        try (
                InputStream htmlStream = getClass().getClassLoader()
                        .getResourceAsStream("util/CuerpoGmailRecuperacion.html")) {
            String codigoRecuperacion = generarCodigoRecuperacion(10);
            String messageSubject = "Recuperación de contraseña";
            String bodyText = loadHtmlContent(htmlStream, codigoRecuperacion);

            Optional<UsuarioModelo> optionalUsuario = usuarioRepositorio.findByEmail(destinatarioEmail);

            if (optionalUsuario.isPresent()) {
                UsuarioModelo usuario = optionalUsuario.get();
                usuario.setRecuperarContrasenia(codigoRecuperacion);

                // Establezco la fecha de expiracion que puse que tenia 3 minutos
                RecuperarContraseniaDTO recuperarContraseniaDTO = new RecuperarContraseniaDTO();
                recuperarContraseniaDTO.setFechaExpiracion();
                usuario.setExpiracionRecuperarContrasenia(recuperarContraseniaDTO.getExpiracionRecuperarContrasenia());
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

    // Este metodo es para leer la plantilla HTML
    private String loadHtmlContent(InputStream inputStream, String codigoRecuperacion) throws IOException {

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            // Reemplazo el marcador de posición con el código real de recuperación que
            // tengo en mi html
            return stringBuilder.toString().replace("{{codigoRecuperacion}}", codigoRecuperacion);
        }
    }

    // Genero mi codigo de recuperacion aleatorio con numeros y letras
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
}