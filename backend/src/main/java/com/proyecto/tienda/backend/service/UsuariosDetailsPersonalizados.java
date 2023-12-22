package com.proyecto.tienda.backend.service;

import java.util.Collection;
import java.util.stream.Collectors;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import com.proyecto.tienda.backend.models.Usuarios;

public class UsuariosDetailsPersonalizados extends User {
    
    private String userId; // Agrego un campo para el ID del usuario

    public UsuariosDetailsPersonalizados(String username, String password, boolean enabled, boolean accountNonExpired,
            boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
            String userId) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }
    public static UsuariosDetailsPersonalizados build(Usuarios usuario) {
        Collection<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName().name()))
                .collect(Collectors.toList());

        return new UsuariosDetailsPersonalizados(
                usuario.getEmail(),
                usuario.getPassword(),
                true,
                true,
                true,
                true,
                authorities,
                usuario.get_id()
        );
    }


    


}

