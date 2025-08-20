-- Actualizar contraseñas con hash BCrypt correcto para "123456"
UPDATE usuarios SET contrasena = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZV.H1/pDKdVAql94c0hKE2' WHERE correo_electronico = 'juan@email.com';
UPDATE usuarios SET contrasena = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZV.H1/pDKdVAql94c0hKE2' WHERE correo_electronico = 'maria@email.com';
UPDATE usuarios SET contrasena = '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXulpZV.H1/pDKdVAql94c0hKE2' WHERE correo_electronico = 'carlos@email.com';