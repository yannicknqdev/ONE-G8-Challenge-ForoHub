package com.alura.forohub.controller;

import com.alura.forohub.domain.respuesta.RespuestaRepository;
import com.alura.forohub.domain.topico.TopicoRepository;
import com.alura.forohub.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/estadisticas")
public class EstadisticasController {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private RespuestaRepository respuestaRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasGenerales() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        estadisticas.put("totalUsuarios", usuarioRepository.count());
        estadisticas.put("totalTopicos", topicoRepository.count());
        estadisticas.put("totalRespuestas", respuestaRepository.count());
        
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/resumen")
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