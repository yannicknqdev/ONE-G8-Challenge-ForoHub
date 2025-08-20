package com.alura.forohub.domain.respuesta;

import jakarta.validation.constraints.NotBlank;

public record DatosActualizacionRespuesta(
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,
        
        Boolean solucion
) {
}