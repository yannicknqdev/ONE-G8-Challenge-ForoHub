package com.alura.forohub.controller;

import com.alura.forohub.domain.curso.Curso;
import com.alura.forohub.domain.curso.CursoRepository;
import com.alura.forohub.domain.topico.DatosActualizacionTopico;
import com.alura.forohub.domain.topico.DatosDetalleTopico;
import com.alura.forohub.domain.topico.DatosListadoTopico;
import com.alura.forohub.domain.topico.DatosRegistroTopico;
import com.alura.forohub.domain.topico.DatosRespuestaTopico;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Optional;

@RestController
@RequestMapping("/topicos")
@Tag(name = "Tópicos", description = "Operaciones relacionadas con la gestión de tópicos del foro")
@SecurityRequirement(name = "Bearer Authentication")
public class TopicoController {

    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private CursoRepository cursoRepository;

    @PostMapping
    @Transactional
    @Operation(summary = "Crear un nuevo tópico", description = "Registra un nuevo tópico en el foro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tópico creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o tópico duplicado"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosRespuestaTopico> registrarTopico(
            @Parameter(description = "Datos del tópico a crear") @RequestBody @Valid DatosRegistroTopico datos) {
        
        // Verificar que no exista un tópico duplicado
        if (topicoRepository.existsByTituloAndMensaje(datos.titulo(), datos.mensaje())) {
            throw new RuntimeException("Ya existe un tópico con el mismo título y mensaje");
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
        
        return ResponseEntity.ok(new DatosRespuestaTopico(topico));
    }

    @GetMapping
    @Operation(summary = "Listar tópicos", description = "Obtiene una lista paginada de tópicos con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de tópicos obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Page<DatosListadoTopico>> listarTopicos(
            @Parameter(description = "Configuración de paginación") @PageableDefault(size = 10, sort = "fechaCreacion", direction = Sort.Direction.ASC) Pageable paginacion,
            @Parameter(description = "Filtrar por nombre del curso") @RequestParam(required = false) String curso,
            @Parameter(description = "Filtrar por año") @RequestParam(required = false) Integer anio) {
        
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
    @Operation(summary = "Obtener detalle de tópico", description = "Obtiene los detalles de un tópico específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tópico encontrado"),
        @ApiResponse(responseCode = "404", description = "Tópico no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosDetalleTopico> detalleTopico(
            @Parameter(description = "ID del tópico") @PathVariable Long id) {
        Topico topico = topicoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tópico no encontrado con ID: " + id));
        
        return ResponseEntity.ok(new DatosDetalleTopico(topico));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Actualizar tópico", description = "Actualiza los datos de un tópico existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Tópico actualizado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Tópico no encontrado"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o tópico duplicado"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosDetalleTopico> actualizarTopico(
            @Parameter(description = "ID del tópico") @PathVariable Long id, 
            @Parameter(description = "Nuevos datos del tópico") @RequestBody @Valid DatosActualizacionTopico datos) {
        
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
    @Operation(summary = "Eliminar tópico", description = "Elimina un tópico del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Tópico eliminado exitosamente"),
        @ApiResponse(responseCode = "404", description = "Tópico no encontrado"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Void> eliminarTopico(
            @Parameter(description = "ID del tópico") @PathVariable Long id) {
        Optional<Topico> topicoOptional = topicoRepository.findById(id);
        
        if (!topicoOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        topicoRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}