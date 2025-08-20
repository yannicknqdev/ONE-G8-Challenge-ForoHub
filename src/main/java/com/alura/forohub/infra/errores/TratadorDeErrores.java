package com.alura.forohub.infra.errores;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class TratadorDeErrores {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity tratarError404() {
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity tratarError400(MethodArgumentNotValidException e) {
        var errores = e.getFieldErrors().stream().map(DatosErrorValidacion::new).toList();
        return ResponseEntity.badRequest().body(errores);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity tratarErrorBadCredentials() {
        return ResponseEntity.status(401).body("Credenciales inválidas");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity tratarErrorAuthentication() {
        return ResponseEntity.status(401).body("Fallo en la autenticación");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity tratarErrorAccesoDenegado() {
        return ResponseEntity.status(403).body("Acceso denegado");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity tratarError500(Exception e) {
        return ResponseEntity.status(500).body("Error interno del servidor: " + e.getLocalizedMessage());
    }

    private record DatosErrorValidacion(String campo, String error) {
        public DatosErrorValidacion(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}