package com.utch.devqa.exception;

import com.utch.devqa.dto.ErrorRespuesta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorGlobalExcepciones {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorRespuesta> capturarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        ErrorRespuesta cuerpo = new ErrorRespuesta(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(cuerpo);
    }

    @ExceptionHandler(DatoDuplicadoException.class)
    public ResponseEntity<ErrorRespuesta> capturarDatoDuplicado(DatoDuplicadoException ex) {
        ErrorRespuesta cuerpo = new ErrorRespuesta(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflicto de datos duplicados",
                ex.getMessage(),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(cuerpo);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRespuesta> capturarErrorValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> erroresCampos = new LinkedHashMap<>();
        for (FieldError errorCampo : ex.getBindingResult().getFieldErrors()) {
            erroresCampos.put(errorCampo.getField(), errorCampo.getDefaultMessage());
        }

        ErrorRespuesta cuerpo = new ErrorRespuesta(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Datos de entrada inválidos",
                "Uno o más campos no cumplen las reglas de validación",
                erroresCampos
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(cuerpo);
    }
}
