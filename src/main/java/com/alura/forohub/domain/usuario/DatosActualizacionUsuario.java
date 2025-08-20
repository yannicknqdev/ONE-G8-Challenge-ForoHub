package com.alura.forohub.domain.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosActualizacionUsuario(
        @NotBlank(message = "El nombre es obligatorio")
        String nombre,
        
        @NotBlank(message = "El correo electrónico es obligatorio")
        @Email(message = "El correo electrónico debe tener un formato válido")
        String correoElectronico,
        
        @NotNull(message = "El perfil es obligatorio")
        Long perfilId
) {
}