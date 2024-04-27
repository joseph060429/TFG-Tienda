package com.proyecto.tienda.backend.ErroresGlobales;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ValidacionErrores {
    
    // MANEJO DE EXCEPCIONES DE VALIDACIÓN (METHODARGUMENTNOTVALIDEXCEPTION)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationException(MethodArgumentNotValidException ex) {

        // Obtengo los resultados de la validación
        BindingResult result = ex.getBindingResult();

        // Creo un mapa para almacenar los errores de validación (campo -> mensaje)
        Map<String, String> errors = new HashMap<>();

         // Verifico si hay errores de validación
        if (result.hasErrors()) {

            // Itero sobre todos los errores de validación
            result.getAllErrors().forEach((error) -> {
                System.err.println(error);
                // Obtengo el nombre del campo que causó el error
                String fieldName = ((FieldError) error).getField();
                // Obtengo el mensaje de error asociado al campo
                String errorMessage = error.getDefaultMessage();
                // Agrego el campo y su mensaje de error al mapa
                errors.put(fieldName, errorMessage);
            });
        }
         // Devuelvo una respuesta HTTP de tipo bad request con el mapa de errores
        return ResponseEntity.badRequest().body(errors);
    }  
}
