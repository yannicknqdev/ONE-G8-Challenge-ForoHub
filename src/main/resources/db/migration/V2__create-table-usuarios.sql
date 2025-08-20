CREATE TABLE usuarios (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    correo_electronico VARCHAR(100) NOT NULL UNIQUE,
    contrasena VARCHAR(300) NOT NULL,
    perfil_id BIGINT NOT NULL,
    
    PRIMARY KEY(id),
    CONSTRAINT fk_usuarios_perfil_id FOREIGN KEY(perfil_id) REFERENCES perfiles(id)
);