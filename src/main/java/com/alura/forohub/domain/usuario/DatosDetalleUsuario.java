package com.alura.forohub.domain.usuario;

public record DatosDetalleUsuario(
        Long id,
        String nombre,
        String correoElectronico,
        String perfil,
        boolean activo,
        int totalTopicos,
        int totalRespuestas
) {
    public DatosDetalleUsuario(Usuario usuario, int totalTopicos, int totalRespuestas) {
        this(
            usuario.getId(),
            usuario.getNombre(),
            usuario.getCorreoElectronico(),
            usuario.getPerfil().getNombre(),
            usuario.isEnabled(),
            totalTopicos,
            totalRespuestas
        );
    }
}