package com.alura.forohub.domain.usuario;

public record DatosListadoUsuario(
        Long id,
        String nombre,
        String correoElectronico,
        String perfil,
        boolean activo
) {
    public DatosListadoUsuario(Usuario usuario) {
        this(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreoElectronico(),
            usuario.getPerfil().getNombre(),
            usuario.isEnabled()
        );
    }
}