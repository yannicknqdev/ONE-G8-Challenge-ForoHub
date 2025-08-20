package com.alura.forohub.controller;

import com.alura.forohub.domain.perfil.Perfil;
import com.alura.forohub.domain.perfil.PerfilRepository;
import com.alura.forohub.domain.usuario.DatosRegistroUsuario;
import com.alura.forohub.domain.usuario.DatosRespuestaUsuario;
import com.alura.forohub.domain.usuario.Usuario;
import com.alura.forohub.domain.usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/registro")
@Tag(name = "Registro", description = "Operaciones de registro de nuevos usuarios")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PerfilRepository perfilRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    @Operation(summary = "Registrar nuevo usuario", description = "Registra un nuevo usuario en el sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario registrado exitosamente",
                content = @Content(schema = @Schema(implementation = DatosRespuestaUsuario.class))),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o correo electrónico ya existe")
    })
    public ResponseEntity<DatosRespuestaUsuario> registrarUsuario(
            @Parameter(description = "Datos del nuevo usuario") @RequestBody @Valid DatosRegistroUsuario datos) {
        
        // Verificar que el email no exista
        if (usuarioRepository.existsByCorreoElectronico(datos.correoElectronico())) {
            throw new RuntimeException("Ya existe un usuario con este correo electrónico");
        }
        
        // Buscar el perfil ESTUDIANTE por defecto
        Perfil perfilEstudiante = perfilRepository.findByNombre("ESTUDIANTE");
        if (perfilEstudiante == null) {
            throw new RuntimeException("Error: Perfil ESTUDIANTE no encontrado");
        }
        
        // Encriptar la contraseña
        String contrasenaEncriptada = passwordEncoder.encode(datos.contrasena());
        
        // Crear y guardar el usuario
        Usuario nuevoUsuario = new Usuario(datos, perfilEstudiante, contrasenaEncriptada);
        usuarioRepository.save(nuevoUsuario);
        
        return ResponseEntity.ok(new DatosRespuestaUsuario(nuevoUsuario));
    }
}