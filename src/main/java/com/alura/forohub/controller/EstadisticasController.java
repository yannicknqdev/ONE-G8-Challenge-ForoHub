package com.alura.forohub.controller;

import com.alura.forohub.domain.respuesta.RespuestaRepository;
import com.alura.forohub.domain.topico.TopicoRepository;
import com.alura.forohub.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/estadisticas")
@Tag(name = "Estadísticas", description = "Operaciones para obtener estadísticas del foro")
@SecurityRequirement(name = "Bearer Authentication")
public class EstadisticasController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private RespuestaRepository respuestaRepository;

    @GetMapping
    @Operation(summary = "Estadísticas generales", description = "Obtiene estadísticas generales del foro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Estadísticas obtenidas exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasGenerales() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("totalUsuarios", usuarioRepository.count());
        estadisticas.put("totalTopicos", topicoRepository.count());
        estadisticas.put("totalRespuestas", respuestaRepository.count());
        
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/resumen")
    @Operation(summary = "Resumen completo", description = "Obtiene un resumen completo de las estadísticas del foro")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resumen obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Map<String, Object>> obtenerResumenCompleto() {
        Map<String, Object> resumen = new HashMap<>();
        
        // Estadísticas básicas
        resumen.put("usuarios", Map.of(
            "total", usuarioRepository.count(),
            "activos", usuarioRepository.count() // Todos están activos por defecto
        ));
        
        resumen.put("topicos", Map.of(
            "total", topicoRepository.count()
        ));
        
        resumen.put("respuestas", Map.of(
            "total", respuestaRepository.count()
        ));
        
        return ResponseEntity.ok(resumen);
    }
}