package com.alura.forohub.infra.security;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record DatosAutenticacionUsuario(
        @NotBlank
        @Email
        String correoElectronico,
        
        @NotBlank
        String contrasena
) {
}