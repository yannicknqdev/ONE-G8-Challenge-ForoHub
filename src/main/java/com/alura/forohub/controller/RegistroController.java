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

@RestController
@RequestMapping("/registro")
public class RegistroController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private PerfilRepository perfilRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping
    @Transactional
    public ResponseEntity<DatosRespuestaUsuario> registrarUsuario(@RequestBody @Valid DatosRegistroUsuario datos) {
        
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