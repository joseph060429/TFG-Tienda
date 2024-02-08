package com.proyecto.tienda.backend.security;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.proyecto.tienda.backend.security.filter.JwtAuthenticationFilter;
import com.proyecto.tienda.backend.security.filter.JwtAuthorizationFilter;
import com.proyecto.tienda.backend.security.jwt.JwtUtils;

@Configuration
// Habilito las anotacionaciones de Spring Security
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    JwtAuthorizationFilter jwtAuthorizationFilter;

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
                    // auth.requestMatchers("/helloSeguridad").permitAll();
                    auth.requestMatchers("/login").permitAll();
                    auth.requestMatchers("/crearUsuario").permitAll();
                    auth.requestMatchers("/admin/*").hasRole("ADMIN");
                    auth.requestMatchers("/envioCodigoRecuperacion").permitAll();
                    auth.requestMatchers("/verificarCodigo").permitAll();
                    auth.requestMatchers("/listarProductos").permitAll();
                    auth.requestMatchers("/buscarPorCamposImportantes").permitAll();
                    auth.requestMatchers("/buscarPorEspecificacion").permitAll();
                    auth.requestMatchers("/buscarPorRangoDePrecio").permitAll();
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

    // Contraseña encriptada
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Esto se encarga de administrar la autenticacion en la aplicacion y nos exige
    // un password encoder
    @Bean
    AuthenticationManager authenticationManager(HttpSecurity httpSecurity, PasswordEncoder passwordEncoder)
            throws Exception {

        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and().build();
    }

    // Configuracion email:
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("imap.ionos.es"); // Configura el servidor SMTP de Ionos
        mailSender.setPort(993); // Configura el puerto (puedes usar 587 o 465 según las configuraciones de
                                 // Ionos)
        mailSender.setUsername("administracion@jastoredcomponents.es"); // Tu dirección de correo de Ionos
        mailSender.setPassword("***REMOVED***"); // Tu contraseña de correo de Ionos

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.debug", "true");

        return mailSender;
    }

}
