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

import java.util.Optional;

@RestController
@RequestMapping("/respuestas")
public class RespuestaController {

    @Autowired
    private RespuestaRepository respuestaRepository;
    
    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    @Transactional
    public ResponseEntity<DatosRespuestaRespuesta> registrarRespuesta(@RequestBody @Valid DatosRegistroRespuesta datos) {
        
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
    public ResponseEntity<Page<DatosListadoRespuesta>> listarRespuestas(
            @PageableDefault(size = 10, sort = "fechaCreacion") Pageable paginacion,
            @RequestParam(required = false) Long topicoId,
            @RequestParam(required = false) Long autorId) {
        
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
    public ResponseEntity<DatosRespuestaRespuesta> detalleRespuesta(@PathVariable Long id) {
        Respuesta respuesta = respuestaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Respuesta no encontrada con ID: " + id));
        
        return ResponseEntity.ok(new DatosRespuestaRespuesta(respuesta));
    }

    @PutMapping("/{id}")
    @Transactional
    public ResponseEntity<DatosRespuestaRespuesta> actualizarRespuesta(
            @PathVariable Long id, 
            @RequestBody @Valid DatosActualizacionRespuesta datos) {
        
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
    public ResponseEntity<Void> eliminarRespuesta(@PathVariable Long id) {
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
    public ResponseEntity<DatosRespuestaRespuesta> marcarComoSolucion(@PathVariable Long id) {
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