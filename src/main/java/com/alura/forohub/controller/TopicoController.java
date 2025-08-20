package com.alura.forohub.controller;

import com.alura.forohub.domain.curso.Curso;
import com.alura.forohub.domain.curso.CursoRepository;
import com.alura.forohub.domain.topico.DatosActualizacionTopico;
import com.alura.forohub.domain.topico.DatosDetalleTopico;
import com.alura.forohub.domain.topico.DatosListadoTopico;
import com.alura.forohub.domain.topico.DatosRegistroTopico;
import com.alura.forohub.domain.topico.Topico;
import com.alura.forohub.domain.topico.TopicoRepository;
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

import org.springframework.data.domain.Sort;

import java.util.Optional;

@RestController
@RequestMapping("/topicos")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<String> registrarTopico(@RequestBody @Valid DatosRegistroTopico datos) {
        
        // Verificar que no exista un tópico duplicado
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            return ResponseEntity.badRequest().body("Ya existe un tópico con el mismo título y mensaje");
        }
        
        // Buscar el autor
        Usuario autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        
        // Buscar el curso
        Curso curso = cursoRepository.findById(datos.cursoId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        // Crear y guardar el tópico
        Topico topico = new Topico(datos, autor, curso);
        topicoRepository.save(topico);
        
        return ResponseEntity.ok("Tópico registrado exitosamente");
    }

    @GetMapping
    public ResponseEntity<Page<DatosListadoTopico>> listarTopicos(
            @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC) Pageable paginacion,
            @RequestParam(required = false) String curso,
            @RequestParam(required = false) Integer anio) {
        
        Page<Topico> topicos;
        
        if (curso != null && anio != null) {
            topicos = topicoRepository.findByCursoNombreAndAnio(curso, anio, paginacion);
        } else if (curso != null) {
            topicos = topicoRepository.findByCursoNombre(curso, paginacion);
        } else if (anio != null) {
            topicos = topicoRepository.findByAnio(anio, paginacion);
        } else {
            topicos = topicoRepository.findAll(paginacion);
        }
        
        return ResponseEntity.ok(topicos.map(DatosListadoTopico::new));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosDetalleTopico> detalleTopico(@PathVariable Long id) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tópico no encontrado con ID: " + id));
        
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosDetalleTopico> actualizarTopico(
            @PathVariable Long id, 
            @RequestBody @Valid DatosActualizacionTopico datos) {
        
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        
        if (!topicoOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Topico topico = topicoOptional.get();
        
        // Verificar que no exista otro tópico con el mismo título y mensaje (excluyendo el actual)
        if (topicoRepository.existsByTituloAndMensajeAndIdNot(datos.titulo(), datos.mensaje(), id)) {
            throw new RuntimeException("Ya existe otro tópico con el mismo título y mensaje");
        }
        
        // Buscar el curso
        Curso curso = cursoRepository.findById(datos.cursoId())
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        
        // Actualizar el tópico
        topico.actualizarDatos(datos, curso);
        
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<Void> eliminarTopico(@PathVariable Long id) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        
        if (!topicoOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        topicoRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}