package com.alura.forohub.domain.respuesta;

import java.time.LocalDateTime;

public record DatosListadoRespuesta(
        Long id,
        String mensaje,
        LocalDateTime fechaCreacion,
        String autor,
        String topico,
        Boolean solucion
) {
    public DatosListadoRespuesta(Respuesta respuesta) {
        this(
            respuesta.getId(),
            respuesta.getMensaje(),
            respuesta.getFechaCreacion(),
            respuesta.getAutor().getNombre(),
            respuesta.getTopico().getTitulo(),
            respuesta.getSolucion()
        );
    }
}