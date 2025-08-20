package com.alura.forohub.domain.respuesta;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DatosRegistroRespuesta(
        @NotBlank(message = "El mensaje es obligatorio")
        String mensaje,
        
        @NotNull(message = "El tópico es obligatorio")
        Long topicoId,
        
        @NotNull(message = "El autor es obligatorio")
        Long autorId,
        
        Boolean solucion
) {
}