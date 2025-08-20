package com.alura.forohub.controller;

import com.alura.forohub.domain.topico.DatosListadoTopico;
import com.alura.forohub.domain.topico.TopicoRepository;
import com.alura.forohub.domain.usuario.DatosListadoUsuario;
import com.alura.forohub.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/buscar")
@Tag(name = "Búsqueda", description = "Operaciones de búsqueda global en el foro")
@SecurityRequirement(name = "Bearer Authentication")
public class BusquedaController {

    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    @Operation(summary = "Búsqueda global", description = "Realiza una búsqueda global en usuarios y tópicos")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Búsqueda realizada exitosamente"),
        @ApiResponse(responseCode = "400", description = "Parámetro de búsqueda requerido"),
        @ApiResponse(responseCode = "401", description = "No autorizado - Token JWT requerido")
    })
    public ResponseEntity<Map<String, Object>> busquedaGlobal(
            @Parameter(description = "Término de búsqueda", required = true) @RequestParam String q,
            @Parameter(description = "Configuración de paginación") @PageableDefault(size = 5) Pageable paginacion) {
        
        Map<String, Object> resultados = new HashMap<>();
        
        // Buscar usuarios por nombre o email
        var usuarios = usuarioRepository.findAll(paginacion)
                .getContent()
                .stream()
                .filter(u -> u.getNombre().toLowerCase().contains(q.toLowerCase()) || 
                           u.getCorreoElectronico().toLowerCase().contains(q.toLowerCase()))
                .map(DatosListadoUsuario::new)
                .limit(5)
                .toList();
        
        // Buscar tópicos por título o mensaje
        var topicos = topicoRepository.findAll(paginacion)
                .getContent()
                .stream()
                .filter(t -> t.getTitulo().toLowerCase().contains(q.toLowerCase()) || 
                           t.getMensaje().toLowerCase().contains(q.toLowerCase()))
                .map(DatosListadoTopico::new)
                .limit(5)
                .toList();
        
        resultados.put("usuarios", usuarios);
        resultados.put("topicos", topicos);
        resultados.put("query", q);
        resultados.put("totalResultados", usuarios.size() + topicos.size());
        
        return ResponseEntity.ok(resultados);
    }
}