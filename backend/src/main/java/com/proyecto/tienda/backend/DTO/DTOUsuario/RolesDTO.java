package com.proyecto.tienda.backend.DTO.DTOUsuario;

import java.util.Set;

import com.proyecto.tienda.backend.UtilEnum.ERol;

import lombok.Data;

@Data
public class RolesDTO {

    private Set <ERol> nuevosRoles;
}
