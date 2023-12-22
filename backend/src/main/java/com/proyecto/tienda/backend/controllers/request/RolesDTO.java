package com.proyecto.tienda.backend.controllers.request;

import java.util.Set;

import com.proyecto.tienda.backend.models.ERol;

import lombok.Data;

@Data
public class RolesDTO {

    private Set <ERol> nuevosRoles;
}
