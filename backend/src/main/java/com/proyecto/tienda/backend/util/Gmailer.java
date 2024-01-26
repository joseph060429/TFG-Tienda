package com.proyecto.tienda.backend.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;
import java.util.Random;
import java.util.Set;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.Message;
// import com.proyecto.tienda.backend.controllers.request.RecuperarContraseniaDTO;
import com.proyecto.tienda.backend.DTO.DTOUsuario.RecuperarContraseniaDTO;
import com.proyecto.tienda.backend.models.UsuarioModelo;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;

@Component
public class Gmailer {

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    // @Autowired
    // private Usuarios usuario;

    private static final String TEST_EMAIL = "sepulmonte29@gmail.com";

    // private static NetHttpTransport httpTransport;

    // private static GsonFactory jsonFactory = GsonFactory.getDefaultInstance();

    private Gmail service;

    public Gmailer() throws Exception {

        NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GsonFactory jsonFactory = GsonFactory.getDefaultInstance();
        service = new Gmail.Builder(httpTransport, jsonFactory, getCredentials(httpTransport, jsonFactory))
                .setApplicationName("Proyecto-Tienda")
                .build();

    }

    // client_secret_558869305771-8q1lohufrmhnmvpb0o2eisrovkh3pfkc.apps.googleusercontent.com.json

    private static Credential getCredentials(final NetHttpTransport httpTransport, GsonFactory jsonFactory)
            throws IOException {
        // Load client secrets.
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(jsonFactory,
                new InputStreamReader(Gmailer.class.getResourceAsStream(
                        "/client_secret_558869305771-8q1lohufrmhnmvpb0o2eisrovkh3pfkc.apps.googleusercontent.com.json")));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, clientSecrets, Set.of(GmailScopes.GMAIL_SEND))
                .setDataStoreFactory(new FileDataStoreFactory(Paths.get("tokens").toFile()))
                .setAccessType("offline")
                .build();

        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();


        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
        // returns an authorized Credential object.
        return credential;
    }

    // Metodo para enviar correos de recuperacion y almacenar en mi base de datos el
    // codigo de recuperacion
    public void enviarEmaildeRecuperacion(String recipientEmail, String subject, String message) throws Exception {
        
        // Genero el código de recuperación
        String codigoRecuperacion = generarCodigoRecuperacion(10);

        // Cargar el contenido del archivo HTML
        try (InputStream htmlStream = getClass().getClassLoader()
                .getResourceAsStream("util/CuerpoGmailRecuperacion.html")) {

            // Creo el contenido del email
            String messageSubject = "Recuperación de contraseña";
            String bodyText = loadHtmlContent(htmlStream, codigoRecuperacion);

            Optional<UsuarioModelo> optionalUsuario = usuarioRepositorio.findByEmail(recipientEmail);

            if (optionalUsuario.isPresent()) {
                UsuarioModelo usuario = optionalUsuario.get();
                usuario.setRecuperarContrasenia(codigoRecuperacion);

                // Llamar al método existente en RecuperarContraseniaDTO para establecer la
                // fecha de expiración formateada
                RecuperarContraseniaDTO recuperarContraseniaDTO = new RecuperarContraseniaDTO();
                recuperarContraseniaDTO.setFechaExpiracion();
                usuario.setExpiracionRecuperarContrasenia(recuperarContraseniaDTO.getExpiracionRecuperarContrasenia());

                usuarioRepositorio.save(usuario);
            }

            System.out.println("Codigo de recuperacion enviado: " + codigoRecuperacion);

            // // Lo codifico como mensaje MIME que
            // se refiere al proceso de configurar y preparar el contenido del mensaje de
            // correo electrónico de acuerdo con el estándar MIME (Multipurpose Internet
            // Mail Extensions). MIME es un estándar que amplía el formato de los mensajes
            // de correo electrónico para admitir texto enriquecido, archivos adjuntos,
            // imágenes y otros tipos de contenido multimedia.
            Properties props = new Properties();
            Session session = Session.getDefaultInstance(props, null);
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress(TEST_EMAIL));
            email.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(recipientEmail));
            email.setSubject(messageSubject);
            email.setContent(bodyText, "text/html; charset=utf-8");

            // Codifico y envuelvo el mensaje MIME en un mensaje de Gmail" lo que quiere
            // decir que
            // este método se encarga de procesar y formatear el
            // mensaje MIME para que sea compatible con la API de Gmail antes de ser
            // enviado.
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            email.writeTo(buffer);
            byte[] rawMessageBytes = buffer.toByteArray();
            String encodedEmail = Base64.encodeBase64URLSafeString(rawMessageBytes);
            Message msg = new Message();
            msg.setRaw(encodedEmail);

            try {
                // Crear y enviar el mensaje
                msg = service.users().messages().send("me", msg).execute();
                System.out.println("Id del mensaje: " + msg.getId());
                System.out.println(msg.toPrettyString());

                } catch (GoogleJsonResponseException e) {
                // TODO(developer) - handle error appropriately
                GoogleJsonError error = e.getDetails();
                if (error.getCode() == 403) {
                // System.err.println("No se puede enviar el mensaje: " + e.getDetails());
                } else {
                throw e;
                }
                }
            
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


// {
//     "type": "authorized_user", 
//     "client_id":  "558869305771-8q1lohufrmhnmvpb0o2eisrovkh3pfkc.apps.googleusercontent.com", 
//     "client_secret":  "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", 
//     "refresh_token":  "1//03bnRPJ18bMaYCgYIARAAGAMSNwF-L9Ir9WVdt23CZaflelvMfYr9nC7D0YAosXp8jnmV6LP0QKBLoiJ6z-CCXV74nQxMPgJHUQU"
// }




   // if (credential.getExpiresInSeconds() != null && credential.getExpiresInSeconds() <= 60) {
        //     // Si el token expira en los próximos 60 segundos (o cualquier otro valor que elijas),
        //     // entonces considera que está cerca de expirar y actualiza el token.
        //     boolean isTokenRefreshed = credential.refreshToken();
        //     if (isTokenRefreshed) {
        //        System.out.println("Token actualizado exitosamente." + isTokenRefreshed);
        
        //     } else {
        //         System.out.println("Token no actualizado."+ isTokenRefreshed);
        //     }

        // }