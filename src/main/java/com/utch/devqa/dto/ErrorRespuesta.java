package com.utch.devqa.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorRespuesta(
        LocalDateTime fechaHora,
        int codigoEstado,
        String tipoError,
        String detalle,
        Map<String, String> camposInvalidos
) {
}

