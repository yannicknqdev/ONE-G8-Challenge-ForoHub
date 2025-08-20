-- Insertar perfiles
INSERT INTO perfiles (nombre) VALUES ('ESTUDIANTE');
INSERT INTO perfiles (nombre) VALUES ('INSTRUCTOR');

-- Insertar usuarios (contraseña: 123456)
INSERT INTO usuarios (nombre, correo_electronico, contrasena, perfil_id) VALUES 
('Juan Pérez', 'juan@email.com', '$2a$10$Y50UaMFOxteibQEYLrwuAOe9cjwtZVQd9HJIDEKj48kGkqDBZ5.8u', 1),
('María García', 'maria@email.com', '$2a$10$Y50UaMFOxteibQEYLrwuAOe9cjwtZVQd9HJIDEKj48kGkqDBZ5.8u', 1),
('Carlos Instructor', 'carlos@email.com', '$2a$10$Y50UaMFOxteibQEYLrwuAOe9cjwtZVQd9HJIDEKj48kGkqDBZ5.8u', 2);

-- Insertar cursos
INSERT INTO cursos (nombre, categoria) VALUES 
('Spring Boot Básico', 'Backend'),
('React Avanzado', 'Frontend'),
('Java Fundamentals', 'Backend'),
('MySQL Database', 'Database');