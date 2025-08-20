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

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/buscar")
public class BusquedaController {

    @Autowired
    private TopicoRepository topicoRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<Map<String, Object>> busquedaGlobal(
            @RequestParam String q,
            @PageableDefault(size = 5) Pageable paginacion) {
        
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