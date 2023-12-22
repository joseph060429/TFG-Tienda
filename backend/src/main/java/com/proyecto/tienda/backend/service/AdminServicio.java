package com.proyecto.tienda.backend.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.proyecto.tienda.backend.models.ERol;
import com.proyecto.tienda.backend.models.Roles;
import com.proyecto.tienda.backend.models.Usuarios;
import com.proyecto.tienda.backend.repositorios.RolesRepositorio;
import com.proyecto.tienda.backend.repositorios.UsuarioRepositorio;

@Service
public class AdminServicio {

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private RolesRepositorio rolesRepositorio;

    // Metodo para listar todos los usuarios
    public List<Usuarios> listarUsuarios() {
        return usuarioRepositorio.findAll();
    }

    // Metodo para borrar usuario siendo Admin
    public String eliminarUsuarioSiendoAdmin(String email) {
        Optional<Usuarios> usuarioOptional = usuarioRepositorio.findByEmail(email);

        if (usuarioOptional.isPresent()) {
            Usuarios usuario = usuarioOptional.get();
            usuarioRepositorio.deleteBy_id(usuario.get_id());
            return "Usuario eliminado correctamente";
        } else {
            return "Usuario no encontrado";
        }
    }

    // REVISAR EL METODO ACTUALIZAR Y EL SERVICIO ACTUALIZAR 

    // Metodo para cambiarle el rol a un usuario //El Optional lo pongo porque puede
    // o no estar un Usuario
//     public String editarRolUsuario(String _id, List<ERol> nuevosRoles) {
        
//         Usuarios usuario = usuarioRepositorio.findBy_id(_id);
//         System.out.println("Usuario: " + usuario);
//         System.out.println("------------------------------------------");
//         if (usuario == null) {
//             return "No existe el usuario en la base de datos";
//         }

//         Set<Roles> roles = nuevosRoles.stream()
//                 .map(this::buscarRolPorNombre)
//                 .collect(Collectors.toSet());

//         usuario.setRoles(roles);
//         System.out.println("Roles: " + roles);
//         System.out.println("------------------------------------------");

//         usuarioRepositorio.save(usuario);
//         System.out.println("Usuario actualizado: " + usuario);

//         return "Rol del usuario actualizado";
// }

// public String editarRolUsuario(String _id, List<ERol> nuevosRoles) {
//     Usuarios usuario = usuarioRepositorio.findBy_id(_id);
//     if (usuario == null) {
//         return "No existe el usuario en la base de datos";
//     }

//     Set<Roles> rolesActualizados = nuevosRoles.stream()
//             .map(this::buscarRolPorNombre)
//             .collect(Collectors.toSet());

//     usuario.setRoles(rolesActualizados);

//     usuarioRepositorio.save(usuario);

//     return "Rol del usuario actualizado";
// }

// private Roles buscarRolPorNombre(ERol nombreRol) {
//     String nombreRolString = nombreRol.toString(); // Convertir el enum a String
    
//     Roles rol = rolesRepositorio.findByName(nombreRol)
//             .orElseThrow(() -> new RuntimeException("Rol no encontrado: " + nombreRol));

//     rol.setName(nombreRolString); // Asignar el nombre del rol en la entidad Roles como String
    
//     return rolesRepositorio.save(rol); // Guardar los cambios
// }


}