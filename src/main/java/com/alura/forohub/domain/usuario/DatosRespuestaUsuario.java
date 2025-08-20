package com.alura.forohub.domain.usuario;

import java.time.LocalDateTime;

public record DatosRespuestaUsuario(
        Long id,
        String nombre,
        String correoElectronico,
        String perfil,
        LocalDateTime fechaRegistro
) {
    public DatosRespuestaUsuario(Usuario usuario) {
        this(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreoElectronico(),
            usuario.getPerfil().getNombre(),
            LocalDateTime.now() // Se puede añadir fechaRegistro a la entidad Usuario si se necesita
        );
    }
}