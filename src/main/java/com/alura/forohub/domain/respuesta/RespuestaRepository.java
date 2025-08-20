package com.alura.forohub.domain.respuesta;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RespuestaRepository extends JpaRepository<Respuesta, Long> {
    
    int countByAutorId(Long autorId);
    
    Page<Respuesta> findByTopicoId(Long topicoId, Pageable pageable);
    
    @Query("SELECT r FROM Respuesta r WHERE r.autor.id = :autorId")
    Page<Respuesta> findByAutorId(@Param("autorId") Long autorId, Pageable pageable);
    
    boolean existsByMensajeAndTopicoId(String mensaje, Long topicoId);
}