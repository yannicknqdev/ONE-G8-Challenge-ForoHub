package com.alura.forohub.domain.usuario;

import com.alura.forohub.domain.perfil.Perfil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "usuarios")
@Entity(name = "Usuario")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String nombre;
    
    @Column(name = "correo_electronico")
    private String correoElectronico;
    
    private String contrasena;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "perfil_id")
    private Perfil perfil;
}