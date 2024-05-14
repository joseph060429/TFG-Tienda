package com.proyecto.tienda.backend.security;

import java.util.Arrays;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;
import com.proyecto.tienda.backend.security.filter.JwtAuthenticationFilter;
import com.proyecto.tienda.backend.security.filter.JwtAuthorizationFilter;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

// Cors
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
// Habilito las anotacionaciones de Spring Security
@EnableGlobalMethodSecurity(prePostEnabled = true) // Habilita el uso de @PreAuthorize y @PostAuthorize
public class SecurityConfig {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UsuarioRepositorio usuarioRepositorio;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

    @Value("${email.ionos}")
    private String emailIonos;

    @Value("${contrasenia.ionos}")
    private String contraseniaIonos;

    // CONFIGURACIÓN DEL FILTRO DE SEGURIDAD PARA GESTIONAR LA AUTENTICACIÓN EN LA
    // APLICACIÓN.
    // SE DEFINE EL USO DEL FILTRO DE AUTENTICACIÓN JWT Y SE CONFIGURAN LAS RUTAS Y
    // PERMISOS DE ACCESO.
    // SE ESPECIFICA EL MANEJO DE SESIONES COMO STATELESS (SIN ESTADO) Y SE AGREGAN
    // LOS FILTROS AL CONTEXTO DE SEGURIDAD.
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpsSecurity, AuthenticationManager authenticationManager)
            throws Exception {

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils, usuarioRepositorio);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        return httpsSecurity
                .csrf(config -> config.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/hello").permitAll();
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/refreshToken").permitAll();
                    auth.requestMatchers("/crearUsuario").permitAll();
                    auth.requestMatchers("/envioCodigoRecuperacion").permitAll();
                    auth.requestMatchers("/verificarCodigo").permitAll();
                    auth.requestMatchers("/listarProductos").permitAll();
                    auth.requestMatchers("/listarUnProducto").permitAll();
                    auth.requestMatchers("/buscarPorCamposImportantes").permitAll();
                    auth.requestMatchers("/buscarPorEspecificacion").permitAll();
                    auth.requestMatchers("/buscarPorRangoDePrecio").permitAll();
                    auth.requestMatchers("/payment/*").permitAll();
                    auth.requestMatchers("/usuarios/pedidos/paypal/*").permitAll();
                    auth.requestMatchers("/cambiarContrasenia").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

    }

    // CORS
    // Variable para almacenar la URL del frontend obtenida del archivo
    // application.properties
    @Value("${frontend.url}")
    private String frontendUrl;

    // Defino un bean que configura el filtro CORS
    @Bean
    public CorsFilter corsFilter() {
        // Creo una fuente de configuración CORS basada en URL
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // Creo una configuración CORS
        CorsConfiguration config = new CorsConfiguration();

        // Permito el uso de credenciales en las solicitudes CORS
        config.setAllowCredentials(true);

        // Configuro los orígenes permitidos para las solicitudes CORS
        config.setAllowedOrigins(Arrays.asList(frontendUrl));

        // Permito cualquier encabezado en las solicitudes CORS
        config.addAllowedHeader("*");

        // Permito cualquier método en las solicitudes CORS
        config.addAllowedMethod("*");

        // Registro la configuración CORS para todas las URL
        source.registerCorsConfiguration("/**", config);

        // Devuelvo un nuevo filtro CORS configurado con la fuente de configuración
        return new CorsFilter(source);
    }

    // CONTRASEÑA ENCRIPTADA
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ESTO SE ENCARGA DE ADMINISTRAR LA AUTENTICACIÓN EN LA APLICACIÓN Y NOS EXIGE
    // UN PASSWORD ENCODER.
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder)
            throws Exception {

        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and().build();
    }

    // CONFIGURACION DEL EMAIL
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("imap.ionos.es"); // Configuro el servidor SMTP de Ionos
        mailSender.setPort(993); // Configuro el puerto
        mailSender.setUsername(emailIonos); // Dirección de correo de Ionos
        mailSender.setPassword(contraseniaIonos); // Contraseña de correo de Ionos
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}

