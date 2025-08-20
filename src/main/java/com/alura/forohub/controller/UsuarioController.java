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

import java.util.Optional;

@RestController
@RequestMapping("/usuarios")
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
    public ResponseEntity<Page<DatosListadoUsuario>> listarUsuarios(
            @PageableDefault(size = 10, sort = "nombre") Pageable paginacion) {
        
        var usuarios = usuarioRepository.findAll(paginacion);
        return ResponseEntity.ok(usuarios.map(DatosListadoUsuario::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosDetalleUsuario> detalleUsuario(@PathVariable Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        // Contar tópicos y respuestas del usuario
        int totalTopicos = topicoRepository.countByAutorId(id);
        int totalRespuestas = respuestaRepository.countByAutorId(id);
        
        return ResponseEntity.ok(new DatosDetalleUsuario(usuario, totalTopicos, totalRespuestas));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosRespuestaUsuario> actualizarUsuario(
            @PathVariable Long id, 
            @RequestBody @Valid DatosActualizacionUsuario datos) {
        
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
    public ResponseEntity<Void> eliminarUsuario(@PathVariable Long id) {
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