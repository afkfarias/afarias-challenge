package com.afarias.event_challenge.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
@Schema(name = "AuditoriaResponseDto", description = "Dto con información de auditoria.")
public class AuditoriaResponseDto {

    @Schema(description = "Identificador de auditoria", example = "2")
    Long id;

    @Schema(description = "Identificador de evento", example = "2")
    Long eventoId;

    @Schema(description = "Identificador externo del evento", example = "EVT-1001")
    String eventId;

    @Schema(description = "Fecha y hora de auditoria", example = "2026-05-20T10:00:00")
    LocalDateTime createdAt;
}
