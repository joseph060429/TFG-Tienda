package com.proyecto.tienda.backend.security;

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
import com.proyecto.tienda.backend.security.filter.JwtAuthenticationFilter;
import com.proyecto.tienda.backend.security.filter.JwtAuthorizationFilter;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

@Configuration
// Habilito las anotacionaciones de Spring Security
@EnableGlobalMethodSecurity(prePostEnabled = true) // Habilita el uso de @PreAuthorize y @PostAuthorize
public class SecurityConfig {

    @Autowired
    JwtUtils jwtUtils;

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

        JwtAuthenticationFilter jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
        jwtAuthenticationFilter.setAuthenticationManager(authenticationManager);
        jwtAuthenticationFilter.setFilterProcessesUrl("/login");

        return httpsSecurity
                .csrf(config -> config.disable())
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/hello").permitAll();
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/crearUsuario").permitAll();
                    auth.requestMatchers("/envioCodigoRecuperacion").permitAll();
                    auth.requestMatchers("/verificarCodigo").permitAll();
                    auth.requestMatchers("/listarProductos").permitAll();
                    auth.requestMatchers("/buscarPorCamposImportantes").permitAll();
                    auth.requestMatchers("/buscarPorEspecificacion").permitAll();
                    auth.requestMatchers("/buscarPorRangoDePrecio").permitAll();
                    auth.requestMatchers("/payment/*").permitAll();
                    auth.requestMatchers("/usuarios/pedidos/paypal/*").permitAll();
                    // auth.requestMatchers("/subirImagen").permitAll();
                    auth.requestMatchers("/cambiarContrasenia").permitAll();
                    // auth.requestMatchers("/crearPedido").permitAll();
                    auth.anyRequest().authenticated();
                })
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilter(jwtAuthenticationFilter)
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();

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
