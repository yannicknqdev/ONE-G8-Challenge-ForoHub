-- Actualizar contraseñas con hash BCrypt correcto para "123456"
UPDATE usuarios SET contrasena = '$2a$10$qDSQKYHUmEKzR4ymRWVEdeoo5/7z0JTyfwJgxv9IU4PWYZQJJEHxS' WHERE correo_electronico = 'juan@email.com';
UPDATE usuarios SET contrasena = '$2a$10$qDSQKYHUmEKzR4ymRWVEdeoo5/7z0JTyfwJgxv9IU4PWYZQJJEHxS' WHERE correo_electronico = 'maria@email.com';
UPDATE usuarios SET contrasena = '$2a$10$qDSQKYHUmEKzR4ymRWVEdeoo5/7z0JTyfwJgxv9IU4PWYZQJJEHxS' WHERE correo_electronico = 'carlos@email.com';