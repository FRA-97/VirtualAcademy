package com.fra.VirtualAcademy.excepciones;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.fra.VirtualAcademy.excepciones.ExcepcionesPersonalizadas.*;

//Maneja todas las excepciones personalizadas de la aplicación de manera centralizada
@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    //Maneja los casos en los que un recurso no es encontrado: HTTP 404 NOT FOUND
    @ExceptionHandler(RecursoNoEncontrado.class)
    public ResponseEntity<String> manejarNoEncontrado(RecursoNoEncontrado ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    //Maneja los casos en los que se intenta crear un recurso duplicado: HTTP 409 CONFLICT
    @ExceptionHandler(RecursoDuplicado.class)
    public ResponseEntity<String> manejarDuplicado(RecursoDuplicado ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    //Maneja los casos de datos inválidos o incompletos: HTTP 400 BAD REQUEST
    @ExceptionHandler(DatosInvalidos.class)
    public ResponseEntity<String> manejarDatosInvalidos(DatosInvalidos ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    //Maneja operaciones no permitidas según la lógica de negocio: HTTP 403 FORBIDDEN
    @ExceptionHandler(OperacionNoPermitida.class)
    public ResponseEntity<String> manejarOperacionNoPermitida(OperacionNoPermitida ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    //Fallback genérico para errores no controlados
    //Esto evita exponer excepciones sin control al cliente: HTTP 500 INTERNAL SERVER ERROR
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> manejarExcepcionGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Error inesperado: " + ex.getMessage());
    }
}
