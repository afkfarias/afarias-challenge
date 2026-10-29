package com.afarias.event_challenge.dto;

import com.afarias.event_challenge.enums.EventoTipo;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDateTime;

@Value
@Builder
@Schema(name = "EventoResponseDto", description = "Dto con información de evento.")
public class EventoResponseDto {

  @Schema(description = "Identificador del evento", example = "2")
  Long id;

  @Schema(description = "Identificador externo del evento", example = "EVT-1001")
  String eventId;

  @Schema(description = "Fecha y hora del evento", example = "2026-05-20T10:00:00")
  LocalDateTime timestamp;

  @Schema(description = "Tipo de evento", example = "FC Barcelona")
  EventoTipo eventType;

  @Schema(description = "Descripción del evento", example = "Production line started")
  String description;
}
