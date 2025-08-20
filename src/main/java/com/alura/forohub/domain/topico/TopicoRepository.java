package com.alura.forohub.domain.topico;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TopicoRepository extends JpaRepository<Topico, Long> {
    
    boolean existsByTituloAndMensaje(String titulo, String mensaje);
    
    boolean existsByTituloAndMensajeAndIdNot(String titulo, String mensaje, Long id);
    
    @Query("SELECT t FROM Topico t WHERE t.curso.nombre = :nombreCurso AND YEAR(t.fechaCreacion) = :anio")
    Page<Topico> findByCursoNombreAndAnio(@Param("nombreCurso") String nombreCurso, @Param("anio") int anio, Pageable pageable);
    
    Page<Topico> findByCursoNombre(String nombreCurso, Pageable pageable);
    
    @Query("SELECT t FROM Topico t WHERE YEAR(t.fechaCreacion) = :anio")
    Page<Topico> findByAnio(@Param("anio") int anio, Pageable pageable);
    
    int countByAutorId(Long autorId);
}