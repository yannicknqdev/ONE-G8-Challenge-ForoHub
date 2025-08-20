package com.alura.forohub.controller;

import com.alura.forohub.domain.perfil.Perfil;
import com.alura.forohub.domain.perfil.PerfilRepository;
import com.alura.forohub.domain.respuesta.RespuestaRepository;
import com.alura.forohub.domain.topico.TopicoRepository;
import com.alura.forohub.domain.usuario.DatosActualizacionUsuario;
import com.alura.forohub.domain.usuario.DatosDetalleUsuario;
import com.alura.forohub.domain.usuario.DatosListadoUsuario;
import com.alura.forohub.domain.usuario.DatosRespuestaUsuario;
import com.alura.forohub.domain.usuario.Usuario;
import com.alura.forohub.domain.usuario.UsuarioRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con la gestión de usuarios")
@SecurityRequirement(name = "Bearer Authentication")
public class UsuarioController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PerfilRepository perfilRepository;
    
    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private RespuestaRepository respuestaRepository;

    @GetMapping
    @Operation(summary = "Listar usuarios", description = "Obtiene una lista paginada de todos los usuarios")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Page<DatosListadoUsuario>> listarUsuarios(
            @Parameter(description = "Configuración de paginación") @PageableDefault(size = 10, sort = "nombre") Pageable paginacion) {
        
        var usuarios = usuarioRepository.findAll(paginacion);
        return ResponseEntity.ok(usuarios.map(DatosListadoUsuario::new));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de usuario", description = "Obtiene los detalles de un usuario específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosDetalleUsuario> detalleUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Contar tópicos y respuestas del usuario
        int totalTopicos = topicoRepository.countByAutorId(id);
        int totalRespuestas = respuestaRepository.countByAutorId(id);
        
        return ResponseEntity.ok(new DatosDetalleUsuario(usuario, totalTopicos, totalRespuestas));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Actualizar usuario", description = "Actualiza los datos de un usuario existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Usuario actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o correo electrónico duplicado"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosRespuestaUsuario> actualizarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id, 
            @Parameter(description = "Nuevos datos del usuario") @RequestBody @Valid DatosActualizacionUsuario datos) {
        
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        
        if (!usuarioOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Usuario usuario = usuarioOptional.get();
        
        // Verificar que no exista otro usuario con el mismo email (excluyendo el actual)
        if (usuarioRepository.existsByCorreoElectronicoAndIdNot(datos.correoElectronico(), id)) {
            throw new RuntimeException("Ya existe otro usuario con este correo electrónico");
        }
        
        // Buscar el perfil
        Perfil perfil = perfilRepository.findById(datos.perfilId())
                .orElseThrow(() -> new RuntimeException("Perfil no encontrado"));
        
        // Actualizar el usuario
        usuario.actualizarDatos(datos, perfil);
        
        return ResponseEntity.ok(new DatosRespuestaUsuario(usuario));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Eliminar usuario", description = "Elimina un usuario del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Usuario eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
        @ApiResponse(responseCode = "400", description = "No se puede eliminar - usuario tiene tópicos o respuestas"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Void> eliminarUsuario(
            @Parameter(description = "ID del usuario") @PathVariable Long id) {
        Optional<Usuario> usuarioOptional = usuarioRepository.findById(id);
        
        if (!usuarioOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        // Verificar si el usuario tiene tópicos o respuestas
        int totalTopicos = topicoRepository.countByAutorId(id);
        int totalRespuestas = respuestaRepository.countByAutorId(id);
        
        if (totalTopicos > 0 || totalRespuestas > 0) {
            throw new RuntimeException("No se puede eliminar el usuario porque tiene tópicos o respuestas asociadas");
        }
        
        usuarioRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}