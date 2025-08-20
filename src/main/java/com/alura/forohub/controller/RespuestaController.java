package com.alura.forohub.controller;

import com.alura.forohub.domain.respuesta.DatosActualizacionRespuesta;
import com.alura.forohub.domain.respuesta.DatosListadoRespuesta;
import com.alura.forohub.domain.respuesta.DatosRegistroRespuesta;
import com.alura.forohub.domain.respuesta.DatosRespuestaRespuesta;
import com.alura.forohub.domain.respuesta.Respuesta;
import com.alura.forohub.domain.respuesta.RespuestaRepository;
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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.Optional;

@RestController
@RequestMapping("/respuestas")
@Tag(name = "Respuestas", description = "Operaciones relacionadas con la gestión de respuestas")
@SecurityRequirement(name = "Bearer Authentication")
public class RespuestaController {

    @Autowired
    private RespuestaRepository respuestaRepository;
    
    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional
    @Operation(summary = "Crear nueva respuesta", description = "Registra una nueva respuesta a un tópico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Respuesta creada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos o respuesta duplicada"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosRespuestaRespuesta> registrarRespuesta(
            @Parameter(description = "Datos de la respuesta") @RequestBody @Valid DatosRegistroRespuesta datos) {
        
        // Verificar que no exista una respuesta duplicada en el mismo tópico
        if (respuestaRepository.existsByMensajeAndTopicoId(datos.mensaje(), datos.topicoId())) {
            throw new RuntimeException("Ya existe una respuesta con el mismo mensaje en este tópico");
        }
        
        // Buscar el tópico
        Topico topico = topicoRepository.findById(datos.topicoId())
                .orElseThrow(() -> new RuntimeException("Tópico no encontrado"));
        
        // Buscar el autor
        Usuario autor = usuarioRepository.findById(datos.autorId())
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        
        // Crear y guardar la respuesta
        Respuesta respuesta = new Respuesta(datos, topico, autor);
        respuestaRepository.save(respuesta);
        
        return ResponseEntity.ok(new DatosRespuestaRespuesta(respuesta));
    }

    @GetMapping
    @Operation(summary = "Listar respuestas", description = "Obtiene una lista paginada de respuestas con filtros opcionales")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de respuestas obtenida exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Page<DatosListadoRespuesta>> listarRespuestas(
            @Parameter(description = "Configuración de paginación") @PageableDefault(size = 10, sort = "fechaCreacion") Pageable paginacion,
            @Parameter(description = "Filtrar por ID del tópico") @RequestParam(required = false) Long topicoId,
            @Parameter(description = "Filtrar por ID del autor") @RequestParam(required = false) Long autorId) {
        
        Page<Respuesta> respuestas;
        
        if (topicoId != null) {
            respuestas = respuestaRepository.findByTopicoId(topicoId, paginacion);
        } else if (autorId != null) {
            respuestas = respuestaRepository.findByAutorId(autorId, paginacion);
        } else {
            respuestas = respuestaRepository.findAll(paginacion);
        }
        
        return ResponseEntity.ok(respuestas.map(DatosListadoRespuesta::new));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener detalle de respuesta", description = "Obtiene los detalles de una respuesta específica")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Respuesta encontrada"),
        @ApiResponse(responseCode = "404", description = "Respuesta no encontrada"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosRespuestaRespuesta> detalleRespuesta(
            @Parameter(description = "ID de la respuesta") @PathVariable Long id) {
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Respuesta no encontrada con ID: " + id));
        
        return ResponseEntity.ok(new DatosRespuestaRespuesta(respuesta));
    }

    @PutMapping("/{id}")
    @Transactional
    @Operation(summary = "Actualizar respuesta", description = "Actualiza el contenido de una respuesta existente")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Respuesta actualizada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Respuesta no encontrada"),
        @ApiResponse(responseCode = "400", description = "Datos inválidos"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosRespuestaRespuesta> actualizarRespuesta(
            @Parameter(description = "ID de la respuesta") @PathVariable Long id, 
            @Parameter(description = "Nuevos datos de la respuesta") @RequestBody @Valid DatosActualizacionRespuesta datos) {
        
        Optional<Respuesta> respuestaOptional = respuestaRepository.findById(id);
        
        if (!respuestaOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Respuesta respuesta = respuestaOptional.get();
        
        // Actualizar la respuesta
        respuesta.actualizarDatos(datos);
        
        return ResponseEntity.ok(new DatosRespuestaRespuesta(respuesta));
    }

    @DeleteMapping("/{id}")
    @Transactional
    @Operation(summary = "Eliminar respuesta", description = "Elimina una respuesta del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Respuesta eliminada exitosamente"),
        @ApiResponse(responseCode = "404", description = "Respuesta no encontrada"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Void> eliminarRespuesta(
            @Parameter(description = "ID de la respuesta") @PathVariable Long id) {
        Optional<Respuesta> respuestaOptional = respuestaRepository.findById(id);
        
        if (!respuestaOptional.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        respuestaRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }

    // Endpoint adicional: marcar respuesta como solución
    @PatchMapping("/{id}/solucion")
    @Transactional
    @Operation(summary = "Marcar como solución", description = "Marca una respuesta como la solución al tópico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Respuesta marcada como solución exitosamente"),
        @ApiResponse(responseCode = "404", description = "Respuesta no encontrada"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<DatosRespuestaRespuesta> marcarComoSolucion(
            @Parameter(description = "ID de la respuesta") @PathVariable Long id) {
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Respuesta no encontrada con ID: " + id));
        
        // Desmarcar otras respuestas del mismo tópico como solución
        var respuestasDelTopico = respuestaRepository.findByTopicoId(respuesta.getTopico().getId(), 
                Pageable.unpaged()).getContent();
        
        for (Respuesta r : respuestasDelTopico) {
            if (!r.getId().equals(id)) {
                r.actualizarDatos(new DatosActualizacionRespuesta(r.getMensaje(), false));
            }
        }
        
        // Marcar esta respuesta como solución
        respuesta.actualizarDatos(new DatosActualizacionRespuesta(respuesta.getMensaje(), true));
        
        return ResponseEntity.ok(new DatosRespuestaRespuesta(respuesta));
    }
}